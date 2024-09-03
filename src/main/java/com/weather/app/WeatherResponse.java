package com.weather.app;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class WeatherResponse {
    private List<Day> days;
    private String resolvedAddress;
    private String name;
    private String location;
    private String address;
    private int i;
    private CurrentCondition currentConditions;

    @Getter
    @Setter
    public static class Day{
        private List<Hours> hours;
        private double tempmax;
        private double tempmin;
        private String datetime;
        private String day;
        private String formattedDate;
        private double temp;
        private String direction;
        private String icon;
        private double feelslike;
        private double dew;
        private double humidity;
        private double windgust;
        private double windspeed;
        private double winddir;
        private double cloudcover;
        private int uvindex;
        private String sunrise;
        private String sunset;
        private String conditions;

        @Getter
        @Setter
        public static class Hours{
            private String datetime;
            private double temp;
            private String conditions;
            private double humidity;
            private double feelslike;
            private double windspeed;
            private double winddir;
            private double windgust;
            private double cloudcover;
            private double dew;
            private double precipprob;
        }
    }


    @Setter
    @Getter
    public static class CurrentCondition {
        private String direction;
        private String icon;
        private String datetime;
        private double temp;
        private double feelslike;
        private double dew;
        private double humidity;
        private double windgust;
        private double windspeed;
        private double winddir;
        private double cloudcover;
        private int uvindex;
        private String sunrise;
        private String sunset;
        private String conditions;
    }
}
