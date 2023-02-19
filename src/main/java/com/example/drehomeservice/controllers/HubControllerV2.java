package com.example.drehomeservice.controllers;

import com.example.drehomeservice.services.HubServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class HubControllerV2 {

    @Autowired
    private HubServiceV2 hubService;

    @GetMapping("/get_devices")
    public String getAllConnectedDevices() {
        return hubService.getAllConnectedDevices();
    }

    @GetMapping("/switch")
    public ResponseEntity<String> switchDevice(@RequestParam(name = "id") String id) {
        String response = hubService.switchDevice(id);
        return ResponseEntity.ok(response);
    }
}
