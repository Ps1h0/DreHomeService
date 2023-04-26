package com.example.drehomeservice.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class AbstractClient {

    public WebClient createWebClient(String url, String token) {
        log.info("Создание web клиента с url: " + url);
        return WebClient.builder().baseUrl(url).defaultHeader("Authorization", token).build();
    }
}
