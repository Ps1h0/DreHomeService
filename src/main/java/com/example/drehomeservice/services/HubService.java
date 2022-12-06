package com.example.drehomeservice.services;

import com.example.drehomeservice.clients.HubClient;
import com.example.drehomeservice.entities.Device;
import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HubService {

    @Autowired
    private HubClient hubClient;

    public String getAllConnectedDevices() {
        return hubClient.getConnectedDevices().toString();
    }

    public Response switchDevice(String id) {
        Device device = hubClient.getDeviceById(Integer.parseInt(id));
        return hubClient.switchDevice(createChangeStatusRequest(device));
    }

    private DeviceChangeStatusRequest createChangeStatusRequest(Device device) {
        DeviceChangeStatusRequest request = new DeviceChangeStatusRequest();
        request.setZcl_id(device.getType().getZclId());
        request.setOppy_key(device.isIncluded() ? 1 : 0);
        request.setParams(device.getType().getZclId() == 768 ? List.of(254, 1) : new ArrayList<>());
        request.setDevices(List.of(device.getDevId()));
        request.setGroups(new ArrayList<>());
        return request;
    }
}
