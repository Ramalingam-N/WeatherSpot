package com.weather.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class WeatherService {
    @Autowired
    private static RestTemplate restTemplate;
    @Autowired
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    String[] cities = {
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
            "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose",
            "Austin", "Jacksonville", "Fort Worth", "Columbus", "Charlotte",
            "San Francisco", "Indianapolis", "Seattle", "Denver", "Washington",
            "Boston", "El Paso", "Nashville", "Detroit", "Oklahoma City",
            "Portland", "Las Vegas", "Memphis", "Louisville", "Baltimore",
            "Milwaukee", "Albuquerque", "Tucson", "Fresno", "Sacramento",
            "Kansas City", "Long Beach", "Mesa", "Atlanta", "Colorado Springs",
            "Virginia Beach", "Raleigh", "Omaha", "Miami", "Oakland",
            "Minneapolis", "Tulsa", "Arlington", "Tampa", "New Orleans",
            "Wichita", "Cleveland", "Bakersfield", "Aurora", "Anaheim",
            "Honolulu", "Santa Ana", "Riverside", "Corpus Christi", "Lexington",
            "Henderson", "Stockton", "Saint Paul", "Cincinnati", "St. Louis",
            "Pittsburgh", "Greensboro", "Anchorage", "Plano", "Lincoln",
            "Orlando", "Irvine", "Newark", "Toledo", "Durham", "Chula Vista",
            "Fort Wayne", "Jersey City", "St. Petersburg", "Laredo", "Madison",
            "Chandler", "Buffalo", "Lubbock", "Scottsdale", "Reno",
            "Glendale", "Gilbert", "Winston-Salem", "North Las Vegas", "Norfolk",
            "Chesapeake", "Garland", "Irving", "Hialeah", "Fremont",
            "Boise", "Richmond", "Baton Rouge", "Spokane", "Des Moines",
            "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Ahmedabad",
            "Chennai", "Kolkata", "Surat", "Pune", "Jaipur",
            "Lucknow", "Kanpur", "Nagpur", "Visakhapatnam", "Indore",
            "Thane", "Bhopal", "Pimpri-Chinchwad", "Patna", "Vadodara",
            "Coimbatore", "Kochi", "Madurai", "Tiruchirappalli", "Salem",
            "Erode", "Tiruppur", "Vellore", "Thoothukudi", "Tirunelveli"
    };

    public WeatherResponse getWeather(String location) {
        String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + location + "?unitGroup=metric&key=ABHFPFLAEVMD2JEBPYU7XEJNN";
        WeatherResponse weatherResponse = null;
        boolean flag1 = false;
        try {
            weatherResponse = restTemplate.getForObject(apiUrl, WeatherResponse.class);
        } catch (Exception e) {
            flag1 = true;
        }
        if (!flag1) {
            String fullName = weatherResponse.getResolvedAddress();
            String name = "";
            String address = "";
            boolean flag = true;
            weatherResponse.setLocation(location);

            for (int i = 0; i < fullName.length(); i++) {
                if (flag && fullName.charAt(i) == ',') {
                    flag = false;
                    continue;
                }
                if (flag)
                    name += fullName.charAt(i);
                else
                    address += fullName.charAt(i);
            }
            weatherResponse.setName(name.toUpperCase());
            weatherResponse.setAddress(address);
            double max = Math.max(weatherResponse.getDays().get(0).getTempmax(), weatherResponse.getCurrentConditions().getTemp());
            double min = Math.min(weatherResponse.getDays().get(0).getTempmin(), weatherResponse.getCurrentConditions().getTemp());

            weatherResponse.getDays().get(0).setTempmax(max);
            weatherResponse.getDays().get(0).setTempmin(min);

            for(int i = 0; i < weatherResponse.getDays().size(); i++){
                String date = weatherResponse.getDays().get(i).getDatetime();
                LocalDate date1 = LocalDate.parse(date);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                weatherResponse.getDays().get(i).setFormattedDate(date1.format(formatter));

                LocalDate dateString = LocalDate.parse(date);
                DayOfWeek dayOfWeek = dateString.getDayOfWeek();
                weatherResponse.getDays().get(i).setDay(dayOfWeek.toString().substring(0, 3));
            }

            double currentTemp = weatherResponse.getCurrentConditions().getTemp();
            weatherResponse.getCurrentConditions().setIcon(tempIcon(currentTemp));

            double windDir = weatherResponse.getCurrentConditions().getWinddir();
            weatherResponse.getCurrentConditions().setDirection(windDirection(windDir));

            for(int i = 0; i < weatherResponse.getDays().size(); i++) {
                double temp = weatherResponse.getDays().get(i).getTemp();
                weatherResponse.getDays().get(i).setIcon(tempIcon(temp));
            }

            for(int i = 0; i < weatherResponse.getDays().size(); i++){
                double winddir = weatherResponse.getDays().get(i).getWinddir();
                weatherResponse.getDays().get(i).setDirection(windDirection(winddir));
            }
        }
        else {
            return null;
        }
        return weatherResponse;
    }


    public List<HourlyWeather> getHourlyWeather(String location){
        String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + location + "?unitGroup=metric&key=LYPXSDSWDXWLZTNMCA5BFZ8GN";
        WeatherResponse weatherResponse = null;
        List<HourlyWeather> list = new ArrayList<>();
        boolean flag1 = false;
        try {
            weatherResponse = restTemplate.getForObject(apiUrl, WeatherResponse.class);
        } catch (Exception e) {
            flag1 = true;
        }
        if (!flag1) {
            String fullName = weatherResponse.getResolvedAddress();
            String name = "";
            String address = "";
            boolean flag = true;

            for (int i = 0; i < fullName.length(); i++) {
                if (flag && fullName.charAt(i) == ',') {
                    flag = false;
                    continue;
                }
                if (flag)
                    name += fullName.charAt(i);
                else
                    address += fullName.charAt(i);
            }

            int i = 0;
            int x = 0;
            int currentTime = Integer.parseInt(weatherResponse.getCurrentConditions().getDatetime().substring(0,2));

            while(i < 24){
                HourlyWeather hourlyWeather = new HourlyWeather();
                hourlyWeather.setAddress(address);
                hourlyWeather.setName(name.toUpperCase());
                hourlyWeather.setLocation(location);

                if(currentTime == 24){
                    currentTime = 0;
                    x = 1;
                }

                String date = weatherResponse.getDays().get(x).getDatetime();
                LocalDate date1 = LocalDate.parse(date);
                LocalDate dateString = LocalDate.parse(date);
                DayOfWeek dayOfWeek = dateString.getDayOfWeek();
                String day = dayOfWeek.toString().substring(0, 3);

                hourlyWeather.setDay(day);

                String icon = tempIcon(weatherResponse.getDays().get(x).getHours().get(currentTime).getTemp());
                String direction = windDirection(weatherResponse.getDays().get(x).getHours().get(currentTime).getWinddir());

                hourlyWeather.setTime(weatherResponse.getDays().get(x).getHours().get(currentTime).getDatetime().substring(0,5));
                hourlyWeather.setDay(day);
                hourlyWeather.setIcon(icon);
                hourlyWeather.setTemp(weatherResponse.getDays().get(x).getHours().get(currentTime).getTemp());
                hourlyWeather.setConditions(weatherResponse.getDays().get(x).getHours().get(currentTime).getConditions());
                hourlyWeather.setFeelslike(weatherResponse.getDays().get(x).getHours().get(currentTime).getFeelslike());
                hourlyWeather.setWindspeed(weatherResponse.getDays().get(x).getHours().get(currentTime).getWindspeed());
                hourlyWeather.setWindgust(weatherResponse.getDays().get(x).getHours().get(currentTime).getWindgust());
                hourlyWeather.setDirection(direction);
                hourlyWeather.setHumidity(weatherResponse.getDays().get(x).getHours().get(currentTime).getHumidity());
                hourlyWeather.setDew(weatherResponse.getDays().get(x).getHours().get(currentTime).getDew());
                hourlyWeather.setPrecipprob(weatherResponse.getDays().get(x).getHours().get(currentTime).getPrecipprob());
                hourlyWeather.setCloudcover(weatherResponse.getDays().get(x).getHours().get(currentTime).getCloudcover());
                i++;
                currentTime++;
                list.add(hourlyWeather);
            }
        }
        else{
            return null;
        }
        return list;
    }

    public WeatherResponse getHistoricalWeather(String location, String inputDate){
        String startDate = "";
        String endDate = "";
        String timeZoneApiUrl = "https://api.ipgeolocation.io/timezone?apiKey=f7e7de221f284d288f678c9ecf0e32ba&location="+location;
        TimeZone timeZone = null;
        boolean dateCheck = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            timeZone = restTemplate.getForObject(timeZoneApiUrl, TimeZone.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                LocalDate date = LocalDate.parse(inputDate, formatter);
                LocalDate yesterday = LocalDate.now().minusDays(1);
                LocalDate fiveDaysAfter = date.plusDays(4);
                if (fiveDaysAfter.isAfter(yesterday)) {
                    endDate = formatter.format(yesterday);
                    startDate = formatter.format(yesterday.minusDays(4));
                    dateCheck = true;
                } else {
                    endDate = formatter.format(fiveDaysAfter);
                    startDate = inputDate;
                }
            } else {
                LocalDate date = LocalDate.parse(inputDate, formatter);
                LocalDate yesterday = LocalDate.now().minusDays(1);
                LocalDate fiveDaysAfter = date.plusDays(4);
                if (fiveDaysAfter.isAfter(yesterday)) {
                    endDate = formatter.format(yesterday);
                    startDate = formatter.format(yesterday.minusDays(4));
                    dateCheck = true;
                } else {
                    endDate = formatter.format(fiveDaysAfter);
                    startDate = inputDate;
                }
            }
        }
        int index = -1;

        if(timeZone != null) {
            ZoneId zoneId = ZoneId.of(timeZone.getTimezone());
            ZonedDateTime date = ZonedDateTime.parse(inputDate + "T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))).withZoneSameInstant(zoneId);
            ZonedDateTime yesterday = ZonedDateTime.now(zoneId).minusDays(1);
            ZonedDateTime fiveDaysAfter = date.plusDays(4);
            if (fiveDaysAfter.isAfter(yesterday)) {
                endDate = formatter.format(yesterday.toLocalDate());
                startDate = formatter.format(yesterday.minusDays(4).toLocalDate());
                dateCheck = true;
            }else{
                endDate = formatter.format(fiveDaysAfter.toLocalDate());
                startDate = inputDate;
            }
        }
        String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"+location+"/"+startDate+"/"+endDate+"?unitGroup=metric&key=HQJ9NLES4ZFTUKR686T5AUJZW";
        WeatherResponse weatherResponse = null;
        boolean flag1 = false;
        try {
            weatherResponse = restTemplate.getForObject(apiUrl, WeatherResponse.class);
        } catch (Exception e) {
            flag1 = true;
        }
        if (!flag1) {
            String fullName = weatherResponse.getResolvedAddress();
            String name = "";
            String address = "";
            boolean flag = true;
            weatherResponse.setLocation(location);

            if(dateCheck){
                for(int i = 0; i < weatherResponse.getDays().size(); i++){
                    if(inputDate.equals(weatherResponse.getDays().get(i).getDatetime())){
                        index = i;
                        break;
                    }
                }
            }

            weatherResponse.setI(index);

            for (int i = 0; i < fullName.length(); i++) {
                if (flag && fullName.charAt(i) == ',') {
                    flag = false;
                    continue;
                }
                if (flag)
                    name += fullName.charAt(i);
                else
                    address += fullName.charAt(i);
            }
            weatherResponse.setName(name.toUpperCase());
            weatherResponse.setAddress(address);

            for(int i = 0; i < weatherResponse.getDays().size(); i++){
                String date = weatherResponse.getDays().get(i).getDatetime();
                LocalDate date1 = LocalDate.parse(date);
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                weatherResponse.getDays().get(i).setFormattedDate(date1.format(formatter1));

                LocalDate dateString = LocalDate.parse(date);
                DayOfWeek dayOfWeek = dateString.getDayOfWeek();
                weatherResponse.getDays().get(i).setDay(dayOfWeek.toString().substring(0, 3));

                double temp = weatherResponse.getDays().get(i).getTemp();
                weatherResponse.getDays().get(i).setIcon(tempIcon(temp));

                double winddir = weatherResponse.getDays().get(i).getWinddir();
                weatherResponse.getDays().get(i).setDirection(windDirection(winddir));
            }

        }
        else{
            return null;
        }
        return weatherResponse;
    }


    String windDirection(double winddir) {
        if ((winddir >= 0 && winddir < 22.5) || (winddir >= 337.5 && winddir < 360))
            return "N";
        else if (winddir >= 22.5 && winddir < 45)
            return "NNE";
        else if (winddir >= 45 && winddir < 67.5)
            return "NE";
        else if (winddir >= 67.5 && winddir < 90)
            return "ENE";
        else if (winddir >= 90 && winddir < 112.5)
            return "E";
        else if (winddir >= 112.5 && winddir < 135)
            return "ESE";
        else if (winddir >= 135 && winddir < 157.5)
            return "SE";
        else if (winddir >= 157.5 && winddir < 180)
            return "SSE";
        else if (winddir >= 180 && winddir < 202.5)
            return "S";
        else if (winddir >= 202.5 && winddir < 225)
            return "SSW";
        else if (winddir >= 225 && winddir < 247.5)
            return "SW";
        else if (winddir >= 247.5 && winddir < 270)
            return "WSW";
        else if (winddir >= 270 && winddir < 292.5)
            return "W";
        else if (winddir >= 292.5 && winddir < 315)
            return "WNW";
        else if (winddir >= 315 && winddir < 337.5)
            return "NW";
        else
            return "N";
    }

    String tempIcon(double temp) {
        if (temp <= 24.0)
            return "rainy-day";
        else if (temp >= 32.0)
            return "sun";
        else
            return "cloudy";
    }

    public String getYesterday(String location) {
        String yesterdayDate = "";
        try {
            String timeZoneApiUrl = "https://api.ipgeolocation.io/timezone?apiKey=f7e7de221f284d288f678c9ecf0e32ba&location=" + location;
            TimeZone timeZone = restTemplate.getForObject(timeZoneApiUrl, TimeZone.class);
            ZoneId zoneId = ZoneId.of(timeZone.getTimezone());
            ZonedDateTime yesterday = ZonedDateTime.now(zoneId).minusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return yesterday.format(formatter);
        }
        catch (Exception e) {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            yesterdayDate = yesterday.format(formatter);
        }
            return yesterdayDate;
        }

        public String getRandomCity(){
            Random random = new Random();
            int randomIndex = random.nextInt(130);
            return cities[randomIndex];
        }
}
