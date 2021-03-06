package com.zhevakin.TelegramBotDemo.handlers;

import com.zhevakin.TelegramBotDemo.dao.*;
import com.zhevakin.TelegramBotDemo.enums.BotMessageEnum;
import com.zhevakin.TelegramBotDemo.model.*;
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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Autowired
    WeatherDao weatherDao;


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
        } else if (inputText.contains("/tasks")) {
            return getTasksMessage(chatId, users, inputText);
        } else if (inputText.equals("/weather")) {
            return getWeathersModules(chatId);
        } else if (inputText.contains("/city")) {
            return setCityUsers(chatId,users, inputText);
        } else if (inputText.contains("/forecast")) {
            return getForecast(chatId);
        } else if (inputText.contains("/weather settings")) {
            return setWeatherSettings(chatId, inputText);
        } else if (inputText.contains("/time")) {
            return setTimeReminder(chatId, inputText);
        } else {
            return new SendMessage(chatId, BotMessageEnum.NON_COMMAND_MESSAGE.getMessage());
        }
    }

    private BotApiMethod<?> setTimeReminder(String chatId, String inputText) {
        String[] inputMessage = inputText.split(" ");
        if (inputMessage.length == 1) return new SendMessage(chatId, "????????????????????, ?????????????? ??????????");
        return new SendMessage(chatId, "?????????????????? ?????????? ?????????????????? ?? ");
    }

    private BotApiMethod<?> setWeatherSettings(String chatId, String inputText) {
        boolean everyday = false;
        Users users = usersDao.getById(Long.parseLong(chatId));
        String[] settings = inputText.replace("/weather settings ","").split(";");
        if (settings[0].toLowerCase().equals("????")) everyday = true;
        SimpleDateFormat format = new SimpleDateFormat("hh.mm.ss");
        Date time = null;
        try {
            time = format.parse(settings[1]);
            WeatherModule weatherModule = new WeatherModule();
            weatherModule.setEveryday(everyday);
            weatherModule.setUsers(users);
            weatherModule.setTime(time);
            weatherDao.create(weatherModule);
            return new SendMessage(chatId, BotMessageEnum.SUCCESS_SETTINGS.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
            return new SendMessage(chatId, BotMessageEnum.INVALID_FORMAT_TIME.getMessage());
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

    // TODO: ?????????????? ???????????????????? ?????????????????? ????????????????????
    private BotApiMethod<?> getTasksMessage(String chatId, Users users, String inputText) {

        String[] inputMessage = inputText.split(";");
        Tasks tasks = new Tasks();
        Date curTime = new Date();
        if (inputMessage.length == 1) return new SendMessage(chatId,BotMessageEnum.INFO_TASKS.getMessage());
        else if (inputMessage.length == 3 ){
                tasks.setDateDone(addMinuteToDate(Integer.parseInt(inputMessage[1]), curTime));
                tasks.setText(inputMessage[2]);
            } else  {
                tasks.setDateDone(addMinuteToDate(10,curTime));
                tasks.setText(inputMessage[1]);
            }
            tasks.setTopic("Test topic");
            tasks.setUsers(users);
            tasksDao.create(tasks);
            return new SendMessage(chatId, "?????????????????? ???????????? ??: " + tasks.getDateDone().toString());
        }

    private Date addMinuteToDate(int minutes, Date beforeTime) {
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
        long curTimeIns = beforeTime.getTime();
        return new Date(curTimeIns + (minutes * ONE_MINUTE_IN_MILLIS));
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
