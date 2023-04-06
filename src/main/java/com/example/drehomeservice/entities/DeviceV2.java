package com.example.drehomeservice.entities;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DeviceV2 {
    private int devId;
    private String devName;
    private boolean isIncluded;
    private Type type;

    @AllArgsConstructor
    @Getter
    public enum Type {
        Sensor(1026, "Датчик", 1280),
        Rosette(81, "Розетка", 6),
        Bulb(268, "Лампочка", 6);
        private final int dvtpNum;
        private final String name;
        private final int zclId;
    }
}

