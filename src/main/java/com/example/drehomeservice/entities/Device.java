package com.example.drehomeservice.entities;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Device {
    private int devId;
    private String devName;
    private boolean isIncluded;
    private Type type;

    @AllArgsConstructor
    @Getter
    public enum Type {
        Device(6),
        Sensor(1280);
        private final int zclId;
    }
}
