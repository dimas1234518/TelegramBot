package com.zhevakin.TelegramBotDemo.threads;

import com.zhevakin.TelegramBotDemo.dao.WeatherDao;
import com.zhevakin.TelegramBotDemo.model.Tasks;
import com.zhevakin.TelegramBotDemo.model.WeatherModule;
import com.zhevakin.TelegramBotDemo.model.WriteReadBot;
import com.zhevakin.TelegramBotDemo.modules.weather.template.WeatherRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherThread {

    private List<WeatherModule> currentList;
    private List<WeatherModule> weatherModulesList;
    private final List<WeatherModule> sendingList = new ArrayList<>();

    @Autowired
    private final WeatherDao weatherDao;

    @Autowired
    private final WriteReadBot bot;

    @Autowired
    WeatherRestTemplate weatherRestTemplate;

    public WeatherThread(WeatherDao weatherDao, WriteReadBot bot, WeatherRestTemplate weatherRestTemplate) {
        this.weatherDao = weatherDao;
        this.bot = bot;
        this.weatherRestTemplate = weatherRestTemplate;
        currentList = weatherDao.getAll();
        weatherModulesList = weatherDao.getAll();
    }

    // 60 * 60 * 1000 - один час
    @Scheduled(fixedRate = 30 * 1000)
    public void synchronizedList() {
        weatherModulesList = weatherDao.getAll();
        currentList = weatherModulesList;
        weatherDao.update(sendingList);
        sendingList.clear();
    }

    /* TODO: реализовать, чтобы во время по минутно срабатывал. Т.е. если взяли за целый час, то уведомления будут отправляться
        в 19.01, 19.02, 19.03 и т.д.
     */
    @Scheduled(fixedRate = 5 * 1000)
    public void sendMessage() {
        if (currentList == null) return;
        for (WeatherModule weatherModule : currentList) {
            if (weatherModule.isDone()) continue;
            if (bot.sendMessage(weatherModule.getUsers().getId(), weatherRestTemplate.getForecast(weatherModule.getUsers().getCity())))  {
                weatherModule.setDone(true);
                weatherDao.update(weatherModule);
                sendingList.add(weatherModule);
            }
        }
    }
}
