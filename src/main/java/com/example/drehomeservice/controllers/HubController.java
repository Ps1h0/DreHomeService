package com.example.drehomeservice.controllers;

import com.example.drehomeservice.services.HubService;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/hub")
public class HubController {

    @Autowired
    private HubService hubService;

    @GetMapping("/get_devices")
    public String getAllConnectedDevices() {
        return hubService.getAllConnectedDevices();
    }

    @GetMapping("/switch")
    public Response switchDevice(@RequestParam(name = "id") String id) {
        return hubService.switchDevice(id);
    }
}
