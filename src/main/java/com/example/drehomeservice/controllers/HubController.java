package com.example.drehomeservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HubController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/get_devices")
    public String getAllDevices() {
        return "devices";
    }
}
