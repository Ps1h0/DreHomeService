package com.example.drehomeservice.controllers;

import com.example.drehomeservice.services.HubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private HubService hubService;

    /**
     * Получение списка устройств, подключенных к хабу
     */
    @GetMapping("/devices")
    public ResponseEntity<?> getAllConnectedDevices() {
        return ResponseEntity.ok(hubService.getAllConnectedDevices());
    }

    /**
     * Переключение устройства
     * @param id идентификатор устройства
     */
    @GetMapping("/switch")
    public ResponseEntity<String> switchDevice(@RequestParam(name = "id") String id) {
        String response = hubService.switchDevice(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/longPolling")
    public String longPolling(@RequestParam(name = "id") String id) {
        return hubService.longPolling(id);
    }
}
