package com.zhevakin.TelegramBotDemo.handlers;

import com.zhevakin.TelegramBotDemo.dao.LogsDao;
import com.zhevakin.TelegramBotDemo.dao.ModulesDao;
import com.zhevakin.TelegramBotDemo.dao.TasksDao;
import com.zhevakin.TelegramBotDemo.dao.UsersDao;
import com.zhevakin.TelegramBotDemo.enums.BotMessageEnum;
import com.zhevakin.TelegramBotDemo.model.Logs;
import com.zhevakin.TelegramBotDemo.model.Modules;
import com.zhevakin.TelegramBotDemo.model.Tasks;
import com.zhevakin.TelegramBotDemo.model.Users;
import com.zhevakin.TelegramBotDemo.modules.weather.template.WeatherRestTemplate;
import com.zhevakin.TelegramBotDemo.telegram.TelegramApiClient;
import com.zhevakin.TelegramBotDemo.threads.LogsThread;
import com.zhevakin.TelegramBotDemo.threads.UsersThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component
public class MessageHandler {
    TelegramApiClient telegramApiClient;
    @Autowired
    UsersDao usersDao;
    @Autowired
    LogsDao logsDao;
    @Autowired
    ModulesDao modulesDao;
    @Autowired
    TasksDao tasksDao;


    public MessageHandler(UsersDao usersDao, LogsDao logsDao, ModulesDao modulesDao) {
        this.usersDao = usersDao;
        this.logsDao = logsDao;
        this.modulesDao = modulesDao;
    }

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();
        Users users = getUser(message);

        if (message.hasDocument()) {
            //return addUserDictionary(chatId, message.getDocument().getFileId());
        }

        String inputText = message.getText();

        if (inputText == null) {
            throw new IllegalArgumentException();
        } else if (inputText.equals("/start")) {
            return getStartMessage(chatId);
        } else if (inputText.equals("/modules")) {
            return getModulesMessage(chatId, users);
        } else if (inputText.equals("/tasks")) {
            return getTasksMessage(chatId, users);
        } else if (inputText.equals("/weather")) {
            return getWeathersModules(chatId);
        } else if (inputText.contains("/city")) {
            return setCityUsers(chatId,users, inputText);
        } else if (inputText.contains("/forecast")) {
            return getForecast(chatId);
        }
        else {
            return new SendMessage(chatId, BotMessageEnum.NON_COMMAND_MESSAGE.getMessage());
        }
    }

    private BotApiMethod<?> getForecast(String chatId) {
        Users users = usersDao.getById(Long.parseLong(chatId));
        WeatherRestTemplate weatherRestTemplate = new WeatherRestTemplate();
        if (users.getCity() == null) return new SendMessage(chatId, BotMessageEnum.CITY_NOT_FOUND.getMessage());
        return new SendMessage(chatId, weatherRestTemplate.getForecast(users.getCity()));
    }

    private BotApiMethod<?> setCityUsers(String chatId, Users users, String inputText) {
        String city = inputText.replace("/city ","");
        usersDao.updateCity(users,city);
        return new SendMessage(chatId, BotMessageEnum.CITY_SUCCESSFUL.getMessage() + " " + city);
    }

    private BotApiMethod<?> getWeathersModules(String chatId) {
        Users users = usersDao.getById(Long.parseLong(chatId));
        WeatherRestTemplate weatherRestTemplate = new WeatherRestTemplate();
        if (users.getCity() == null) return new SendMessage(chatId, BotMessageEnum.CITY_NOT_FOUND.getMessage());
        return new SendMessage(chatId, weatherRestTemplate.getInfo(users.getCity()));
    }

    private BotApiMethod<?> getTasksMessage(String chatId, Users users) {
        Tasks tasks = new Tasks();
        tasks.setText("Test text");
        tasks.setTopic("Test topic");
        tasks.setUsers(users);
        Date currentDate = new Date();
        currentDate.setTime(currentDate.getTime() + 60 * 2);
        tasks.setDateDone(currentDate);
        tasksDao.create(tasks);
        return new SendMessage(chatId, "Сообщение придет в: " + currentDate.toString());
    }

    private SendMessage getStartMessage(String chatId) {
        return new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getMessage());
    }

    private SendMessage getModulesMessage(String chatId, Users users) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Modules> modulesList = modulesDao.getAll();
        for (int i = 0; i < modulesList.size(); i++) {
            stringBuilder.append(i+1).append(" ").append(modulesList.get(i).toString()).append("\n");
        }
        return new SendMessage(chatId, stringBuilder.toString());
    }

    private Users getUser(Message message) {
        Users users = new Users();
        User userTelegram = message.getFrom();
        users.setFirstName(userTelegram.getFirstName());
        users.setLastName(userTelegram.getLastName());
        users.setId(userTelegram.getId());
        users.setUserName(userTelegram.getUserName());
        UsersThread usersThread = new UsersThread(users,usersDao);
        usersThread.run();
        getLogs(message,users);

        return users;
    }

    private void getLogs(Message message, Users users) {
        Logs logs = new Logs();
        logs.setAction(message.getText());
        logs.setUsers(users);
        LogsThread logsThread = new LogsThread(logs,logsDao);
        logsThread.run();
    }

    private SendMessage getTask(String chatId) {
        return new SendMessage(chatId, "");
    }
/*
    private SendMessage getTasksMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.CHOOSE_DICTIONARY_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons(
                CallbackDataPartsEnum.TASK_.name(),
                dictionaryExcelService.isUserDictionaryExist(chatId)
        ));
        return sendMessage;
    }

    private SendMessage getDictionaryMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.CHOOSE_DICTIONARY_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtonsWithTemplate(
                CallbackDataPartsEnum.DICTIONARY_.name(),
                dictionaryExcelService.isUserDictionaryExist(chatId)
        ));
        return sendMessage;
    }

    private SendMessage addUserDictionary(String chatId, String fileId) {
        try {
            dictionaryAdditionService.addUserDictionary(chatId, telegramApiClient.getDocumentFile(fileId));
            return new SendMessage(chatId, BotMessageEnum.SUCCESS_UPLOAD_MESSAGE.getMessage());
        } catch (TelegramFileNotFoundException e) {
            return new SendMessage(chatId, BotMessageEnum.EXCEPTION_TELEGRAM_API_MESSAGE.getMessage());
        } catch (DictionaryTooBigException e) {
            return new SendMessage(chatId, BotMessageEnum.EXCEPTION_TOO_LARGE_DICTIONARY_MESSAGE.getMessage());
        } catch (Exception e) {
            return new SendMessage(chatId, BotMessageEnum.EXCEPTION_BAD_FILE_MESSAGE.getMessage());
        }
    }

 */
}
