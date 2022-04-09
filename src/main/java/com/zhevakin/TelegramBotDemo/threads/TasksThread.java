package com.zhevakin.TelegramBotDemo.threads;

import com.zhevakin.TelegramBotDemo.dao.TasksDao;
import com.zhevakin.TelegramBotDemo.handlers.MessageHandler;
import com.zhevakin.TelegramBotDemo.model.Tasks;
import com.zhevakin.TelegramBotDemo.model.WriteReadBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TasksThread {

    private List<Tasks> currentTasksList;
    private List<Tasks> tasksList;
    @Autowired
    private final TasksDao tasksDao;
    @Autowired
    WriteReadBot bot;


    public TasksThread(TasksDao tasksDao, WriteReadBot bot) {
        this.tasksDao = tasksDao;
        this.bot = bot;
        tasksList = tasksDao.getAll();
    }
    // 60 * 60 * 1000 - один час
    @Scheduled(fixedRate = 30 * 1000)
    public void synchronizedTasks() {
        tasksList = tasksDao.getAll();
        currentTasksList = tasksList;
    }
    /* TODO: реализовать, чтобы во время по минутно срабатывал. Т.е. если взяли за целый час, то уведомления будут отправляться
            в 19.01, 19.02, 19.03 и т.д.
         */
    @Scheduled(fixedRate = 5 * 1000)
    public void sendMessage() {
        if (currentTasksList == null) return;
        for (Tasks tasks : currentTasksList) {
            if (tasks.isDone()) continue;
            if (bot.sendMessage(tasks.getUsers().getId(), tasks.getText()))  {
                tasks.setDone(true);
                tasksDao.update(tasks,true);
            }
        }
    }

}
