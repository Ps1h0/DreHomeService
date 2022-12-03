package com.example.drehomeservice.requests;

import com.example.drehomeservice.entities.Device;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
public class DeviceChangeStatusRequest {

    private Device device;

    public String getRequest() {
        int oppy_key = 0;
        if (device.isIncluded()) oppy_key = 1;
        return "{\"zcl_id\":" + device.getType().getZclId() + ","
                + "\"oppy_key\":" + oppy_key + ","
                + "\"params\": [],"
                + "\"devices\": [" + device.getDevId() + "],"
                + "\"groups\":[]"
                + "}";
    }
}
