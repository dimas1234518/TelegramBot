package com.zhevakin.TelegramBotDemo.threads;

import com.zhevakin.TelegramBotDemo.dao.LogsDao;
import com.zhevakin.TelegramBotDemo.model.Logs;
import org.springframework.beans.factory.annotation.Autowired;

public class LogsThread{

    private final Logs logs;

    @Autowired
    private final LogsDao logsDao;

    public LogsThread(Logs logs, LogsDao logsDao) {
        this.logs = logs;
        this.logsDao = logsDao;
    }

    public void run(){
        logsDao.create(logs);
    }
}
