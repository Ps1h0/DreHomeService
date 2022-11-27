package com.example.drehomeservice.services;

import com.example.drehomeservice.clients.HubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HubService {

    @Autowired
    private HubClient hubClient;

    public String getAllConnectedDevices() {
        return hubClient.getConnectedDevices().toString();
    }
}
