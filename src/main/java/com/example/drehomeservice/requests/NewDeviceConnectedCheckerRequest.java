package com.example.drehomeservice.requests;

import com.example.drehomeservice.interfaces.HubApiInterface;
import feign.Response;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.HashMap;

@RequiredArgsConstructor
public class NewDeviceConnectedCheckerRequest extends Thread {

    @NonNull
    private String token;
    @NonNull
    private HubApiInterface service;
    @NonNull
    private String url;
    @Getter
    private Response response;

    @Override
    public void run() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        response = service.addDevicesToMap(headers, URI.create(url));
        while (response == null) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
