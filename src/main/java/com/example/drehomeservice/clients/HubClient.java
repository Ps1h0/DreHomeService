package com.example.drehomeservice.clients;

import com.example.drehomeservice.entities.Device;
import com.example.drehomeservice.exceptions.DeviceNotFoundException;
import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
import com.jayway.jsonpath.JsonPath;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubClient extends AbstractClient {

    private static final String DEV_IDS = "$..dev_id";
    private static final String DEV_NAMES = "$..dev_name";
    private static final String DVTP_NUMS = "$..dvtp_num";
    private static final String IS_DEVICE_INCLUDED = "$..zcluster[1].attributes[0].str_attr_value";
    private static final String IS_SENSOR_INCLUDED = "$..zcluster[1].attributes[2].str_attr_value";

    @Autowired
    TaskScheduler taskScheduler;

    @Value("${hub.ip}")
    String url;

    @Value("${token}")
    String token;

    WebClient webClient;

    Map<Integer, Device> connectedDevices;
    int numberOfDevices;
    Map<String, Map<Integer, Device>> schedulerMap = new HashMap<>();

    @PostConstruct
    private void init() {
        log.info("Шаг Init");
        webClient = createWebClient(url, token);
        connectedDevices = getConnectedDevicesFromHub();
    }

    /**
     * Получение подключенных к хабу устройств.
     * Выполняется по расписанию каждые 5 секунд
     */
    private Map<Integer, Device> getConnectedDevicesFromHub() {
        String response = webClient.
                get()
                .uri(String.join("", url, "/v1.3/smarthome/devices"))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        taskScheduler.schedule(
                () -> {
                    connectedDevices = getDevicesFromResponse(response);
                    numberOfDevices = connectedDevices.size();
                }, new CronTrigger("0/5 * * * * *", TimeZone.getDefault().toZoneId())
        );
        Map<Integer, Device> devices = getDevicesFromResponse(response);
        schedulerMap.put("getConnectedDevicesFromHub - " + System.currentTimeMillis(), devices);
        return devices;
    }

    public String switchDevice(DeviceChangeStatusRequest request) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return webClient
                .post()
                .uri(String.join("", url, "/v1.3/smarthome/opportunity"))
                .body(Mono.just(request), DeviceChangeStatusRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Device.Type setTypeOfDevice(int dvtpNum) {
        if (dvtpNum == 1026)
            return Device.Type.Sensor;
        if (dvtpNum == 81)
            return Device.Type.Rosette;
        if (dvtpNum == 268)
            return Device.Type.Bulb;
        else
            return null;
    }

    public Device getDeviceById(int id) {
        return Optional.of(connectedDevices.get(id)).orElseThrow(DeviceNotFoundException::new);
    }

    public Map<Integer, Device> getConnectedDevices() {
        return getConnectedDevicesFromHub();
    }

    private Map<Integer, Device> getDevicesFromResponse(String response) {
        Map<Integer, Device> connectedDevices = new HashMap<>();
        JSONArray devIds = JsonPath.read(response, DEV_IDS);
        JSONArray devNames = JsonPath.read(response, DEV_NAMES);
        JSONArray dvtpNums = JsonPath.read(response, DVTP_NUMS);
        JSONArray isDeviceIncluded = JsonPath.read(response, IS_DEVICE_INCLUDED);
        JSONArray isSensorIncluded = JsonPath.read(response, IS_SENSOR_INCLUDED);

        for (int i = 0; i < devIds.size(); i++) {
            Device device = new Device();
            device.setDevId((Integer) devIds.get(i));
            device.setDevName((String) devNames.get(i));
            device.setType(setTypeOfDevice((Integer) dvtpNums.get(i)));
            if (device.getType().getName().equals("Датчик")) {
                device.setIncluded(isSensorIncluded.get(i).equals("33"));
            } else {
                device.setIncluded(isDeviceIncluded.get(i).equals("1"));
            }
            connectedDevices.put((Integer) devIds.get(i) , device);
        }
        return connectedDevices;
    }

    public String longPolling(String id) {
        Device currentDevice = connectedDevices.get(Integer.parseInt(id));
        int included = currentDevice.isIncluded() ? 1 : 0;
        String response;
        while (true) {
            response = webClient
                    .get()
                    .uri(String.join("", url, "/v1.3/smarthome/devices?dev_id=", id))
                    .retrieve()
                    .bodyToFlux(String.class)
                    .timeout(Duration.ofSeconds(1))
                    .blockFirst();
            JSONArray array = JsonPath.read(response, IS_DEVICE_INCLUDED);
            int statusOfDevice = Integer.parseInt(array.get(0).toString());

            if (statusOfDevice != included) {
                currentDevice.setIncluded(statusOfDevice == 1);
                break;
            }
            else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return currentDevice.toString();
    }
}
