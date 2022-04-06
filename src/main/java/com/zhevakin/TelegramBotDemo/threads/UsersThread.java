package com.zhevakin.TelegramBotDemo.threads;

import com.zhevakin.TelegramBotDemo.dao.UsersDao;
import com.zhevakin.TelegramBotDemo.model.Users;
import org.springframework.beans.factory.annotation.Autowired;

public class UsersThread{

    private final Users user;

    private final UsersDao usersDao;

    public UsersThread(Users user, UsersDao usersDao) {
        this.user = user;
        this.usersDao = usersDao;
    }

    public void run() {
        usersDao.create(user);
    }
}
