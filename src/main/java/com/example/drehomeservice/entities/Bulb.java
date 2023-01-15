package com.example.drehomeservice.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Bulb extends Device {
    private int brightness;
}
