package com.example.drehomeservice.clients;

import com.example.drehomeservice.entities.Device;
import com.example.drehomeservice.interfaces.HubApiInterface;
import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
import feign.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Реализация через okHttpClient
 */
@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubClient extends AbstractClient {

    @Autowired
    TaskScheduler taskScheduler;

    @Value("${hub.ip}")
    String url;

    @Value("${token}")
    String token;

    OkHttpClient httpClient;

    Map<Integer, Device> connectedDevices;
    HubApiInterface service;
    Map<String, Map<Integer, Device>> schedulerMap = new HashMap<>();

    @PostConstruct
    private void init() {
        log.info("Шаг Init");
        httpClient = createHttpClient();
        service = createHubApiInterface(url);
        connectedDevices = getConnectedDevicesFromHub();
        checkStatusesOfSensors();
    }

    private void checkStatusesOfSensors() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        headers.put("Content-Type", "application/json");
        Map<Integer, Device> devices = getConnectedDevices();
        List<Device> sensors = getSensorsFromConnectedDevices(devices);
        for (int i = 0; i < sensors.size(); i++) {
            Response response = service.manageDevice(headers, URI.create(url), String.valueOf(sensors.get(i).getDevId()));
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
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        Response response = service.addDevicesToMap(headers, URI.create(url));
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

    public Response switchDevice(DeviceChangeStatusRequest request) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        headers.put("Content-Type", "application/json");
        return service.switchDevice(headers, URI.create(url), request);
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

    private HubApiInterface createHubApiInterface(String url) {
        log.info("Создание ApiInterface с url:" + url);
        return createApiService(httpClient, HubApiInterface.class, url);
    }

    public Map<Integer, Device> getConnectedDevices() {
        log.info("Получение подключенных устройств");
        return getConnectedDevicesFromHub();
    }

    private Map<Integer, Device> getDevicesFromResponse(Response response) {
        log.info("Получение устройств из ответа\n" + response.toString());
        try {
            InputStream inputStream = response.body().asInputStream();
            String responseDetails = IOUtils.toString(inputStream, Charsets.toCharset(StandardCharsets.UTF_8));
            JSONArray jsonArray = new JSONArray(responseDetails);
            inputStream.close();
            return createDevices(jsonArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
