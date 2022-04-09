package com.zhevakin.TelegramBotDemo.modules.weather.model.standard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Main {

    private float temp;
    private float feels_like;
    private float temp_min;
    private float temp_max;
    private int pressure;
    private int humidity;

}
