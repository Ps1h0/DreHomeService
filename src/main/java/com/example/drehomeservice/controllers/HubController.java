package com.example.drehomeservice.controllers;

import com.example.drehomeservice.services.HubService;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hub")
public class HubController {

    @Autowired
    private HubService hubService;

    @GetMapping("/get_devices")
    public String getAllConnectedDevices() {
        return hubService.getAllConnectedDevices();
    }

    @GetMapping("/switch")
    public ResponseEntity<String> switchDevice(@RequestParam(name = "id") String id) {
        Response response = hubService.switchDevice(id);
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/test2")
    public String test2() {
        return "Дергаю сервис";
    }
}
