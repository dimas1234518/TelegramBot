package com.zhevakin.TelegramBotDemo.modules.weather.template;

import com.zhevakin.TelegramBotDemo.modules.weather.model.forecast.Forecast;
import com.zhevakin.TelegramBotDemo.modules.weather.model.forecast.ForecastWeather;
import com.zhevakin.TelegramBotDemo.modules.weather.model.standard.CurrentWeather;
import com.zhevakin.TelegramBotDemo.modules.weather.model.standard.Main;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.List;

// TODO: https://api.openweathermap.org/data/2.5/forecast?q=Ростов-на-Дону&appid=d5f0f5bc324feb3869e04e63fa86f113&units=metric&lang=RU для прогноза

@Component
public class WeatherRestTemplate {

    // TODO: сделать вывод погоды в конкретный час
    // TODO: изменить Hardcode на аннотацию
    //@Value("${weather.appid}")
    String appid = "d5f0f5bc324feb3869e04e63fa86f113";
    String units = "metric";
    String lang = "RU";

    public String getInfo(String city) {
        RestTemplate restTemplate = new RestTemplate();
        String URL = "https://api.openweathermap.org/data/2.5/weather?";
        String stringBuilder = URL + "q=" + city + "&appid=" + appid +
                "&units=" + units + "&lang=" + lang;
        ResponseEntity<CurrentWeather> responseEntity = restTemplate.getForEntity(stringBuilder, CurrentWeather.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            CurrentWeather currentWeather = responseEntity.getBody();
            //TODO: округлить вывод данных до целых по правилам математики
            return "Погода в городе " + currentWeather.getName() + " на текущий момент времени равна" +
                    " " + (int) currentWeather.getMain().getTemp();
        }
        return "Нет информации о погоде";
    }

    public String getForecast(String city) {

        RestTemplate restTemplate = new RestTemplate();
        String URL = "https://api.openweathermap.org/data/2.5/forecast?";
        String stringBuilder = URL + "q=" + city + "&appid=" + appid + "&units=" + units + "&lang=" + lang;
        ResponseEntity<ForecastWeather> responseEntity = restTemplate.getForEntity(stringBuilder, ForecastWeather.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            ForecastWeather forecastWeather = responseEntity.getBody();
            return getInfo(city) + "\nПогода в городе " + city + " на ближайшие 12 часов " + getDailyWeather(forecastWeather);
        }
        return "Нет информации о погоде";
    }

    @SneakyThrows
    private String getDailyWeather(ForecastWeather forecastWeather) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat stringFormat = new SimpleDateFormat("HH:mm");
        int temp_min = Integer.MAX_VALUE;
        int temp_max = Integer.MIN_VALUE;
        StringBuilder output = new StringBuilder();
        List<Forecast> forecastList = forecastWeather.getList();
        int index = 0;
        for (Forecast forecast : forecastList) {
            index++;
            Main main = forecast.getMain();
            if (temp_min > main.getTemp()) temp_min = (int) main.getTemp();
            if (temp_max < main.getTemp()) temp_max = (int) main.getTemp();
            String outTime = stringFormat.format(dateFormat.parse(forecast.getDt_txt().split(" ")[1]));
            output.append(outTime).append(" ").append((int)main.getTemp()).append(" градусов. Ожидаемая погода: ")
            .append(forecast.getWeather().get(0).getDescription()).append("\n");
            if (index == 5) break;
        }
        return "минимум: " + temp_min + " максимум: " + temp_max + " градусов\n" + output;

    }


}
