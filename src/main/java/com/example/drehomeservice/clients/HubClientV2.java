package com.example.drehomeservice.clients;

import com.example.drehomeservice.entities.Device;
import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
import feign.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.*;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubClientV2 extends AbstractClient {

    @Autowired
    TaskScheduler taskScheduler;

    @Value("${hub.ip}")
    String url;

    @Value("${token}")
    String token;

    WebClient webClient;

    Map<Integer, Device> connectedDevices;
    Map<String, Map<Integer, Device>> schedulerMap = new HashMap<>();

    @PostConstruct
    private void init() {
        webClient = createWebClient(url, token);
        connectedDevices = getConnectedDevicesFromHub();
        checkStatusesOfSensors();
    }

    private void checkStatusesOfSensors() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Map<Integer, Device> devices = getConnectedDevices();
        List<Device> sensors = getSensorsFromConnectedDevices(devices);
        for (int i = 0; i < sensors.size(); i++) {

        }
    }

    private List<Device> getSensorsFromConnectedDevices(Map<Integer, Device> devices) {
        List<Device> sensors = new ArrayList<>();
        for (Map.Entry<Integer, Device> entry : devices.entrySet()) {
            Device current = entry.getValue();
            if (current.getType().getZclId() == 1280)
                sensors.add(current);
        }
        return sensors;
    }

    /**
     * Проверка хаба на подключение новых устройств
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
                    int size = getDevicesFromResponse(response).size();
                    if (connectedDevices.size() < size) {
                        log.info("Подключено новое устройство");
                    } else if (connectedDevices.size() > size) {
                        log.info("Отключено одно из устройств");
                    }
                }, new CronTrigger("0/5 * * * * *", TimeZone.getDefault().toZoneId())
        );
        Map<Integer, Device> devices = getDevicesFromResponse(response);
        schedulerMap.put("getConnectedDevicesFromHub - " + System.currentTimeMillis(), devices);
        return devices;
    }

    public String switchDevice(DeviceChangeStatusRequest request) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String response = webClient
                .post()
                .uri(String.join("", url, "/v1.3/smarthome/opportunity"))
                .body(Mono.just(request), DeviceChangeStatusRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;
    }

    private Map<Integer, Device> createDevices(JSONArray jsonArray) {
        HashMap<Integer, Device> connectedDevices = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentJson = jsonArray.getJSONObject(i);
            int devId = currentJson.getInt("dev_id");
            String devName = currentJson.getString("dev_name");
            Device.Type devType = null;
            JSONArray cluster = currentJson.getJSONArray("zcluster");
            for (int j = 0; j < cluster.length(); j++) {
                JSONObject k = cluster.getJSONObject(j);
                if (k.getInt("zcl_id") == Device.Type.Bulb.getZclId()) {
                    devType = Device.Type.Bulb;
                } else if (k.getInt("zcl_id") == Device.Type.Device.getZclId()) {
                    devType = Device.Type.Device;
                } else if (k.getInt("zcl_id") == Device.Type.Sensor.getZclId()) {
                    devType = Device.Type.Sensor;
                }
            }
            connectedDevices.put(devId, new Device(devId, devName, false, devType));
        }
        return connectedDevices;
    }

    public Device getDeviceById(int id) {
        return Optional.of(connectedDevices.get(id)).orElseThrow(RuntimeException::new);
    }


    public Map<Integer, Device> getConnectedDevices() {
        return getConnectedDevicesFromHub();
    }

    private Map<Integer, Device> getDevicesFromResponse(String response) {
        JSONArray jsonArray = new JSONArray(response);
        return createDevices(jsonArray);
    }
}
