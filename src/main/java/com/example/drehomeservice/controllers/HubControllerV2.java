package com.example.drehomeservice.controllers;

import com.example.drehomeservice.services.HubServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class HubControllerV2 {

    @Autowired
    private HubServiceV2 hubService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Получение списка устройств, подключенных к хабу
     */
    @GetMapping("/api/devices")
    public ResponseEntity<?> getAllConnectedDevices() {
        return ResponseEntity.ok(hubService.getAllConnectedDevices());
    }

    @GetMapping("/get_devices")
    public String getAllDevices() {
        return "devices";
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

    @GetMapping("/test")
    public String test(@RequestParam(name = "id") String id, @RequestParam(name = "test", required = false) String flag) {
        if (flag == null) {
            return hubService.test(id, -1);
        }
        return hubService.test(id, Integer.parseInt(flag));
    }
}
