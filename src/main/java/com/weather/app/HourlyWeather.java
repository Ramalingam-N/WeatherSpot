package com.weather.app;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HourlyWeather {
    private String name;
    private String location;
    private String address;
    private String time;
    private String day;
    private String icon;
    private String direction;
    private double temp;
    private String conditions;
    private double humidity;
    private double feelslike;
    private double windspeed;
    private double windgust;
    private double cloudcover;
    private double dew;
    private double precipprob;
}
