package com.zhevakin.TelegramBotDemo.modules.weather.model.forecast;

import com.zhevakin.TelegramBotDemo.modules.weather.model.standard.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Forecast {

    private long dt;
    private Main main;
    private List<Weather> weather;
    private Clouds clouds;
    private Wind wind;
    private int visibility;
    private int pop;
    private Sys sys;
    private String dt_txt;

}
