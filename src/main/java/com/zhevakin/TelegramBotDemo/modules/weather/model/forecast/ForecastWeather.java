package com.zhevakin.TelegramBotDemo.modules.weather.model.forecast;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ForecastWeather implements Serializable {

    private String cod;
    private int message;
    private int cnt;
    private List<Forecast> list;

}
