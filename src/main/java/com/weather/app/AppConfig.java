package com.weather.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import  java.util.TimeZone;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public TimeZone timeZone() {
//        return TimeZone.getDefault(); // Or specify a default time zone if needed
//    }
}
