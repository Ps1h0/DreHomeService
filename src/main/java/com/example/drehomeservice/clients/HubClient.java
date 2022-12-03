package com.example.drehomeservice.clients;

import com.example.drehomeservice.interfaces.HubApiInterface;
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class HubClient extends AbstractClient {

    @Value("${hub.ip}")
    private String url;

    @Value("${token}")
    private String token;

    private OkHttpClient httpClient;
    @Getter
    private Map<Integer, String> connectedDevices;
    private HubApiInterface service;
    private NewDeviceConnectedCheckerRequest request;

    @PostConstruct
    private void init() {
        httpClient = createHttpClient();
        service = createHubApiInterface(url);
        request = new NewDeviceConnectedCheckerRequest(token, service, url);
        connectedDevices = getConnectedDevicesFromHub();
    }

    private Map<Integer, String> getConnectedDevicesFromHub() {
        request.run();
        Response response = request.getResponse();
        return getDevicesIdsFromResponse(response);
    }

    private Map<Integer, String> getDevicesIdsFromResponse(Response response) {
        Map<Integer, String> connectedDevices = new HashMap<>();
        try (InputStream inputStream = response.body().asInputStream()) {
            String responseDetails = IOUtils.toString(inputStream, Charsets.toCharset(StandardCharsets.UTF_8));
            JSONArray jsonArray = new JSONArray(responseDetails);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentJson = jsonArray.getJSONObject(i);
                connectedDevices.put(currentJson.getInt("dev_id"), currentJson.getString("dev_name"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return connectedDevices;
    }

    private HubApiInterface createHubApiInterface(String url) {
        return createApiService(httpClient, HubApiInterface.class, url);
    }
}
