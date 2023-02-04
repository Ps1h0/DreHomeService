package com.example.drehomeservice.interfaces;

import com.example.drehomeservice.requests.DeviceChangeStatusRequest;
import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.util.Map;

@FeignClient("hub")
@feign.Headers({"Accept: application/json"})
public interface HubApiInterface {

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    default String handleHttpMediaTypeNotAcceptableException() {
        return "acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE;
    }
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
    Response switchDevice(@HeaderMap Map<String, String> headers, URI path, @RequestBody DeviceChangeStatusRequest request);
}
