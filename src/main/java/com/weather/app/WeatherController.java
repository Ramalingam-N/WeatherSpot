package com.weather.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.TimeZone;
import java.util.List;

@Controller
public class WeatherController {
    private WeatherService weatherService;
    private TimeZone timeZone;
    @Autowired
    public WeatherController(WeatherService weatherService){
        this.weatherService = weatherService;
    }
    WeatherResponse prevWeatherResponse;
    WeatherResponse prevHistoricalWeather;
    List<HourlyWeather> prevHourlyWeather;

    @GetMapping("/")
    public String temp(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(Model model){
        String location = weatherService.getRandomCity();
        WeatherResponse weatherResponse = weatherService.getWeather(location);
        if(weatherResponse == null){
            if(prevWeatherResponse == null)
                return "home";
            String yesterday = weatherService.getYesterday( prevWeatherResponse.getLocation());
            model.addAttribute("weather", prevWeatherResponse);
            model.addAttribute("location", prevWeatherResponse.getLocation());
            model.addAttribute("date", yesterday);
            return "current-weather";
        }
        String yesterday = weatherService.getYesterday(location);
        model.addAttribute("weather", weatherResponse);
        model.addAttribute("location", location);
        model.addAttribute("date", yesterday);
        prevWeatherResponse = weatherResponse;
        return "current-weather";
    }
    @GetMapping("/current-weather")
    public String mainPage(@RequestParam(name = "location") String location, Model model){
        WeatherResponse weatherResponse = weatherService.getWeather(location);
        if(weatherResponse == null){
            if(prevWeatherResponse == null)
                return "home";
            String yesterday = weatherService.getYesterday(prevWeatherResponse.getLocation());
            model.addAttribute("weather", prevWeatherResponse);
            model.addAttribute("location", prevWeatherResponse.getLocation());
            model.addAttribute("date", yesterday);
            return "current-weather";
        }
        String yesterday = weatherService.getYesterday(location);
        model.addAttribute("weather", weatherResponse);
        model.addAttribute("location", location);
        model.addAttribute("date", yesterday);
        prevWeatherResponse = weatherResponse;
        return "current-weather";
    }

    @GetMapping("/daily-weather")
    public String dailyWeather(@RequestParam(name = "location") String location, Model model){
        WeatherResponse weatherResponse = weatherService.getWeather(location);
        if(weatherResponse == null){
            if(prevWeatherResponse == null)
                return "home";
            String yesterday = weatherService.getYesterday(prevWeatherResponse.getLocation());
            model.addAttribute("weather", prevWeatherResponse);
            model.addAttribute("location", prevWeatherResponse.getLocation());
            model.addAttribute("date", yesterday);
            return "daily-weather";
        }
        String yesterday = weatherService.getYesterday(location);
        model.addAttribute("date", yesterday);
        model.addAttribute("weather", weatherResponse);
        model.addAttribute("location", location);
        prevWeatherResponse = weatherResponse;
        return "daily-weather";
    }

    @GetMapping("/hourly-weather")
    public String hourlyWeather(@RequestParam(name = "location") String location, Model model){
        List<HourlyWeather> list = weatherService.getHourlyWeather(location);
        if(list == null){
            if(prevHourlyWeather == null)
                return "home";
            String yesterday = weatherService.getYesterday(prevHourlyWeather.get(0).getLocation());
            model.addAttribute("weather", prevHourlyWeather);
            model.addAttribute("location", prevHourlyWeather.get(0).getLocation());
            model.addAttribute("date", yesterday);
            return "hour-weather";
        }
        String yesterday = weatherService.getYesterday(location);
        model.addAttribute("date", yesterday);
        model.addAttribute("weather", list);
        model.addAttribute("location", location);
        prevHourlyWeather = list;
        return "hour-weather";
    }

    @GetMapping("/historical-weather")
    public String historicalWeather(@RequestParam(name = "location") String location,
                                    @RequestParam("date") String date, Model model){
        WeatherResponse weatherResponse = weatherService.getHistoricalWeather(location, date);
        if(weatherResponse == null || weatherResponse.getDays().get(0).getTempmax() == 0){
            if(weatherResponse != null && weatherResponse.getDays().get(0).getTempmax() == 0){
                WeatherResponse modifiedWeatherResponse = weatherService.getHistoricalWeather(location, "2010-01-01");
                model.addAttribute("weather", modifiedWeatherResponse);
                model.addAttribute("location", location);
                model.addAttribute("flag", true);
                prevHistoricalWeather = modifiedWeatherResponse;
                return "historical-weather";
            }
            if(prevHistoricalWeather == null)
                return "home";
            String yesterday = weatherService.getYesterday(prevHistoricalWeather.getLocation());
            model.addAttribute("weather", prevHistoricalWeather);
            model.addAttribute("location", prevHistoricalWeather.getLocation());
            model.addAttribute("date", yesterday);
            return "historical-weather";
        }
        model.addAttribute("weather", weatherResponse);
        model.addAttribute("location", location);
        prevHistoricalWeather = weatherResponse;
        return "historical-weather";
    }

}
