package com.example.drehomeservice.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DeviceChangeStatusRequest {

    private int zcl_id;
    private int oppy_key;
    private List<Integer> params;
    private List<Integer> devices;
    private List<Integer> groups;
}
