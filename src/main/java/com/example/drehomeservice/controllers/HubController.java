package com.example.drehomeservice.controllers;

import com.example.drehomeservice.services.HubService;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hub")
@Slf4j
public class HubController {

    @Autowired
    private HubService hubService;

    @GetMapping("/get_devices")
    public String getAllConnectedDevices() {
        log.info("запрос get_devices");
        return hubService.getAllConnectedDevices();
    }

    @GetMapping("/switch")
    public ResponseEntity<String> switchDevice(@RequestParam(name = "id") String id) {
        log.info("запрос switch");
        Response response = hubService.switchDevice(id);
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/test2")
    public String test2() {
        return "Дергаю сервис";
    }
}
