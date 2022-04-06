package com.zhevakin.TelegramBotDemo.handlers;

import com.zhevakin.TelegramBotDemo.dao.LogsDao;
import com.zhevakin.TelegramBotDemo.dao.ModulesDao;
import com.zhevakin.TelegramBotDemo.dao.UsersDao;
import com.zhevakin.TelegramBotDemo.enums.BotMessageEnum;
import com.zhevakin.TelegramBotDemo.model.Logs;
import com.zhevakin.TelegramBotDemo.model.Users;
import com.zhevakin.TelegramBotDemo.telegram.TelegramApiClient;
import com.zhevakin.TelegramBotDemo.threads.LogsThread;
import com.zhevakin.TelegramBotDemo.threads.UsersThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class MessageHandler {
    TelegramApiClient telegramApiClient;
    @Autowired
    UsersDao usersDao;
    @Autowired
    LogsDao logsDao;
    @Autowired
    ModulesDao modulesDao;


    public MessageHandler(UsersDao usersDao, LogsDao logsDao, ModulesDao modulesDao) {
        this.usersDao = usersDao;
        this.logsDao = logsDao;
        this.modulesDao = modulesDao;
    }

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();

        Users users = getUser(message);
        Logs logs = getLogs(message,users);


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
        }
        else {
            return new SendMessage(chatId, BotMessageEnum.NON_COMMAND_MESSAGE.getMessage());
        }
    }

    private SendMessage getStartMessage(String chatId) {
        return new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getMessage());
    }

    private SendMessage getModulesMessage(String chatId, Users users) {
        return new SendMessage(chatId, modulesDao.getUsersModules(users).toString());
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

        return users;
    }

    private Logs getLogs(Message message, Users users) {
        Logs logs = new Logs();
        logs.setAction(message.getText());
        logs.setUsers(users);
        LogsThread logsThread = new LogsThread(logs,logsDao);
        logsThread.run();
        return logs;
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
