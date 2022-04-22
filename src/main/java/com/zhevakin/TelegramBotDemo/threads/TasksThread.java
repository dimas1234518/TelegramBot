package com.zhevakin.TelegramBotDemo.threads;

import com.zhevakin.TelegramBotDemo.dao.TasksDao;
import com.zhevakin.TelegramBotDemo.dao.UsersDao;
import com.zhevakin.TelegramBotDemo.handlers.MessageHandler;
import com.zhevakin.TelegramBotDemo.model.Tasks;
import com.zhevakin.TelegramBotDemo.model.Users;
import com.zhevakin.TelegramBotDemo.model.WriteReadBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TasksThread {

    private List<Tasks> currentTasksList;
    private List<Tasks> tasksList;
    @Autowired
    private final TasksDao tasksDao;
    @Autowired
    WriteReadBot bot;
    @Autowired
    private final UsersDao usersDao;

    private int index = 0;

    private List<Users> usersList;

    private final List<String> phrases = new ArrayList<>();


    public TasksThread(TasksDao tasksDao, WriteReadBot bot, UsersDao usersDao) {
        this.tasksDao = tasksDao;
        this.usersDao = usersDao;
        this.bot = bot;
        tasksList = tasksDao.getAll();
        usersList = usersDao.getAll();

        phrases.add("Второй Закон Ньютона:\n" +
                "F=ma");
        phrases.add("Третий закон Ньютона:\n" +
                "F=-F");
        phrases.add("Правило по русскому языку:\n" +
                "НЕ с глаголами пишется раздельно, за исключением некоторых случаев. НЕ пишется слитно: 1) в глаголах, которые не употребляются без НЕ: ненавидеть, невзлюбить, негодовать, недоумевать; 2) в глаголах с приставкой НЕДО-*: недосолить суп, недоедать во время засухи.");

    }
    // 60 * 60 * 1000 - один час
    @Scheduled(fixedRate = 60 * 1000)
    public void synchronizedTasks() {
        tasksList = tasksDao.getAll();
        currentTasksList = tasksList;
        usersList = usersDao.getAll();
    }
    /* TODO: реализовать, чтобы во время по минутно срабатывал. Т.е. если взяли за целый час, то уведомления будут отправляться
            в 19.01, 19.02, 19.03 и т.д.
         */

  /*  @Scheduled(fixedRate = 60 * 2 * 1000)
    public void sendReminder() {
        for (Users user : usersList) {
            bot.sendMessage(user.getId(), phrases.get(index));
        }
        index++;
        if (index == phrases.size()) index = 0;
    }

   */

    @Scheduled(fixedRate = 45 * 1000)
    public void sendMessage() {
        if (currentTasksList == null) return;
        for (Tasks tasks : currentTasksList) {
            if (tasks.isDone()) continue;
            long taskTime = tasks.getDateDone().getTime();
            Date currTime = new Date();

            if (taskTime > addMinuteToDate(-2,currTime).getTime() && taskTime < addMinuteToDate(2,currTime).getTime())
                if (bot.sendMessage(tasks.getUsers().getId(), tasks.getText()))  {
                    tasks.setDone(true);
                    tasksDao.update(tasks,true);
                }
        }
    }

    private Date addMinuteToDate(int minutes, Date beforeTime) {
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
        long curTimeIns = beforeTime.getTime();
        return new Date(curTimeIns + (minutes * ONE_MINUTE_IN_MILLIS));
    }

}
