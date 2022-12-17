package com.example.drehomeservice.controllers;

import com.example.drehomeservice.services.HubService;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class HubController {

    @Autowired
    private HubService hubService;

    @GetMapping("/test")
    public String test(){
        System.out.println("TEST");
        return "TEST";
    }

    @GetMapping("/get_devices")
    public String getAllConnectedDevices() {
        return hubService.getAllConnectedDevices();
    }

    @GetMapping("/switch")
    public ResponseEntity<String> switchDevice(@RequestParam(name = "id") String id) {
        Response response = hubService.switchDevice(id);
        System.out.println(response);
        return ResponseEntity.ok(response.toString());
    }
}
