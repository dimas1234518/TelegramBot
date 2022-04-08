package com.zhevakin.TelegramBotDemo.modules.weather.template;

import com.zhevakin.TelegramBotDemo.modules.weather.model.CurrentWeather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherRestTemplate {

    // TODO: change Hardcode
    //@Value("${weather.appid}")
    String appid = "d5f0f5bc324feb3869e04e63fa86f113";

    public String getInfo(String city) {
        RestTemplate restTemplate = new RestTemplate();
        String URL = "https://api.openweathermap.org/data/2.5/weather?";
        String units = "metric";
        String lang = "RU";
        String stringBuilder = URL + "q=" + city + "&appid=" + appid +
                "&units=" + units + "&lang=" + lang;
        ResponseEntity<CurrentWeather> responseEntity = restTemplate.getForEntity(stringBuilder, CurrentWeather.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            CurrentWeather currentWeather = responseEntity.getBody();
            return "Погода в городе " + currentWeather.getName() + " на текущий момент времени равна" +
                    " " + currentWeather.getMain().getTemp();
        }
        return "Нет информации о погоде";
    }

}
