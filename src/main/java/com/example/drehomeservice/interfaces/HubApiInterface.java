package com.example.drehomeservice.interfaces;

import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;

import java.net.URI;
import java.util.Map;

@FeignClient("hub")
public interface HubApiInterface {

    /**
     * Метод для получения идентификаторов устройств в хабе
     */
    @RequestLine(value = "GET /v1.3/smarthome/devices")
    Response addDevicesToMap(@HeaderMap Map<String, String> headers, URI path);

    /**
     * Метод для обработки изменения состояния датчика или получения информации о статусе устройства (вкл/выкл)
     */
    @RequestLine(value = "GET /v1.3/smarthome/devices?dev_id={dev_id}")
    Response manageDevice(@HeaderMap Map<String, String> headers, URI path, @Param("dev_id") String devId);

    /**
     * Метод для изменения состояния устройства (вкл -> выкл; выкл -> вкл)
     */
    @RequestLine(value = "POST /v1.3/smarthome/opportunity")
    Response setPowerOnDevice(@HeaderMap Map<String, String> headers, URI path);
}
