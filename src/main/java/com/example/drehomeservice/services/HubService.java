//package com.example.drehomeservice.services;
//
//import com.example.drehomeservice.clients.HubClient;
//import com.example.drehomeservice.entities.Device;
//import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
//import feign.Response;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Реализация через HttpClient
// */
//@Service
//public class HubService {
//
//    @Autowired
//    private HubClient hubClient;
//
//    public String getAllConnectedDevices() {
//        return hubClient.getConnectedDevices().toString();
//    }
//
//    public Response switchDevice(String id) {
//        Device device = hubClient.getDeviceById(Integer.parseInt(id));
//        DeviceChangeStatusRequest request = createChangeStatusRequest(device);
//        device.setIncluded(!device.isIncluded());
//        return hubClient.switchDevice(request);
//    }
//
//    private DeviceChangeStatusRequest createChangeStatusRequest(Device device) {
//        DeviceChangeStatusRequest request = new DeviceChangeStatusRequest();
//        request.setZcl_id(device.getType().getZclId());
//        request.setOppy_key(device.isIncluded() ? 0 : 1);
//        request.setParams(setParamsForDevice(device));
//        request.setDevices(List.of(device.getDevId()));
//        request.setGroups(new ArrayList<>());
//        return request;
//    }
//
//    private List<Integer> setParamsForDevice(Device device) {
//        if (device.getType().getZclId() == 8) {
//            if (device.isIncluded()) return List.of(254, 1);
//            else return new ArrayList<>();
//        } else if (device.getType().getZclId() == 6) {
//            return new ArrayList<>();
//        }
//        return new ArrayList<>();
//    }
//}
