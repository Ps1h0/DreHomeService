package com.example.drehomeservice.services;

import com.example.drehomeservice.clients.HubClient;
import com.example.drehomeservice.entities.Device;
import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HubService {

    @Autowired
    private HubClient hubClient;

    public String getAllConnectedDevices() {
        return hubClient.getConnectedDevices().toString();
    }

    public Response switchDevice(String id) {
        Device device = hubClient.getDeviceById(Integer.parseInt(id));
        DeviceChangeStatusRequest request = new DeviceChangeStatusRequest(device);
        return hubClient.switchDevice(request.getRequest());
    }
}
