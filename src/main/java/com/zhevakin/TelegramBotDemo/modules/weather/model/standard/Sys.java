package com.zhevakin.TelegramBotDemo.modules.weather.model.standard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sys {

    private int type;
    private int id;
    private float message;
    private String country;
    private long sunrise;
    private long sunset;
}
