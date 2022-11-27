package com.example.drehomeservice.clients;

import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractClient {

    public OkHttpClient createHttpClient() {
        log.info("Создание http клиента");
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
                .build();
    }

    protected <T> T createApiService(OkHttpClient httpClient, Class<T> apiType, String url) {
        return Feign.builder().client(new feign.okhttp.OkHttpClient(httpClient))
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .options(new Request.Options(
                        60, TimeUnit.MILLISECONDS,
                        60, TimeUnit.MILLISECONDS,
                        true
                ))
                .target(apiType, url);
    }
}
