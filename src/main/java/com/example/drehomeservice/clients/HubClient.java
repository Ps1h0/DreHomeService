package com.example.drehomeservice.clients;

import com.example.drehomeservice.entities.Device;
import com.example.drehomeservice.interfaces.HubApiInterface;
import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
import com.example.drehomeservice.requests.NewDeviceConnectedCheckerRequest;
import feign.Response;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class HubClient extends AbstractClient {

    @Value("${hub.ip}")
    private String url;

    @Value("${token}")
    private String token;

    private OkHttpClient httpClient;
    @Getter
    private Map<Integer, Device> connectedDevices;
    private HubApiInterface service;
    private NewDeviceConnectedCheckerRequest request;

    @PostConstruct
    private void init() {
        httpClient = createHttpClient();
        service = createHubApiInterface(url);
        request = new NewDeviceConnectedCheckerRequest(token, service, url);
        connectedDevices = getConnectedDevicesFromHub();
    }

    private Map<Integer, Device> getConnectedDevicesFromHub() {
        request.run();
        return getDevicesFromResponse(request.getResponse());
    }

    public Response switchDevice(DeviceChangeStatusRequest request){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        return service.switchDevice(headers, URI.create(url), request);
    }

    private Map<Integer, Device> getDevicesFromResponse(Response response) {
        try (InputStream inputStream = response.body().asInputStream()) {
            String responseDetails = IOUtils.toString(inputStream, Charsets.toCharset(StandardCharsets.UTF_8));
            JSONArray jsonArray = new JSONArray(responseDetails);
            return createDevices(jsonArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                if (k.getInt("zcl_id") == Device.Type.Bulb.getZclId()){
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
        Optional<Device> optionalDevice = Optional.of(connectedDevices.get(id));
        return optionalDevice.orElseThrow(RuntimeException::new);
    }

    private HubApiInterface createHubApiInterface(String url) {
        return createApiService(httpClient, HubApiInterface.class, url);
    }
}
