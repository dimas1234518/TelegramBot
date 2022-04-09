package com.zhevakin.TelegramBotDemo.modules.weather.model.forecast;

import com.zhevakin.TelegramBotDemo.modules.weather.model.standard.Coord;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class City {

    private long id;
    private String name;
    private Coord coord;
    private String country;
    private long population;
    private long timezone;
    private long sunrise;
    private long sunset;

}
