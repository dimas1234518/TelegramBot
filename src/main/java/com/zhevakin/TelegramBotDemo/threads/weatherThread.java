package com.zhevakin.TelegramBotDemo.threads;

import com.zhevakin.TelegramBotDemo.dao.WeatherDao;
import com.zhevakin.TelegramBotDemo.model.Tasks;
import com.zhevakin.TelegramBotDemo.model.WeatherModule;
import com.zhevakin.TelegramBotDemo.model.WriteReadBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class weatherThread {

    private List<WeatherModule> currentList;
    private List<WeatherModule> weatherModulesList;

    @Autowired
    private final WeatherDao weatherDao;

    @Autowired
    private final WriteReadBot bot;

    public weatherThread(WeatherDao weatherDao, WriteReadBot bot) {
        this.weatherDao = weatherDao;
        this.bot = bot;
        currentList = weatherDao.getAll();
        weatherModulesList = weatherDao.getAll();
    }

    // 60 * 60 * 1000 - один час
   // @Scheduled(fixedRate = 30 * 1000)
    public void synchronizedTasks() {
        weatherModulesList = weatherDao.getAll();
        currentList = weatherModulesList;
    }

 //   @Scheduled(fixedRate = 5 * 1000)
    public void sendMessage() {
        if (currentList == null) return;
        for (WeatherModule weatherModule : currentList) {
            if (weatherModule.isDone()) continue;
            if (bot.sendMessage(weatherModule.getUsers().getId(), weatherModule.getTime().toString()))  {
                weatherModule.setDone(true);
                weatherDao.update(weatherModule,true);
            }
        }
    }
}
