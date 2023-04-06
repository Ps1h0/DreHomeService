package com.example.drehomeservice.services;

import com.example.drehomeservice.clients.HubClientV2;
import com.example.drehomeservice.entities.DeviceV2;
import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Реализация через WebClient
 */
@Component
public class HubServiceV2 {

    @Autowired
    private HubClientV2 hubClient;

    public List<DeviceV2> getAllConnectedDevices() {
        Map<Integer, DeviceV2> devices = hubClient.getConnectedDevices();
        return new ArrayList<>(devices.values());
    }

    public String switchDevice(String id) {
        DeviceV2 device = hubClient.getDeviceById(Integer.parseInt(id));
        DeviceChangeStatusRequest request = createChangeStatusRequest(device);
        device.setIncluded(!device.isIncluded());
        return hubClient.switchDevice(request);
    }

    private DeviceChangeStatusRequest createChangeStatusRequest(DeviceV2 device) {
        DeviceChangeStatusRequest request = new DeviceChangeStatusRequest();
        request.setZcl_id(device.getType().getZclId());
        request.setOppy_key(device.isIncluded() ? 0 : 1);
        request.setParams(setParamsForDevice(device));
        request.setDevices(List.of(device.getDevId()));
        request.setGroups(new ArrayList<>());
        return request;
    }

    private List<Integer> setParamsForDevice(DeviceV2 device) {
        if (device.getType().getZclId() == 8) {
            if (device.isIncluded()) return List.of(254, 1);
            else return new ArrayList<>();
        } else if (device.getType().getZclId() == 6) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public String test(String id, int flag) {
        return hubClient.test(id, flag);
    }
}
