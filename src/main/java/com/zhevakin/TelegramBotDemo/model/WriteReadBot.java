package com.zhevakin.TelegramBotDemo.model;

import com.zhevakin.TelegramBotDemo.enums.BotMessageEnum;
import com.zhevakin.TelegramBotDemo.handlers.MessageHandler;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.IOException;

@Getter
@Setter
public class WriteReadBot extends SpringWebhookBot {

    String botPath;
    String botUsername;
    String botToken;

    MessageHandler messageHandler;
 //   CallbackQueryHandler callbackQueryHandler;

    public WriteReadBot(SetWebhook setWebhook, MessageHandler messageHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
    //    this.callbackQueryHandler = callbackQueryHandler;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
        return handleUpdate(update);
    } catch (IllegalArgumentException e) {
        return new SendMessage(update.getMessage().getChatId().toString(),
                BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE.getMessage());
    } catch (Exception e) {
        return new SendMessage(update.getMessage().getChatId().toString(),
                BotMessageEnum.EXCEPTION_WHAT_THE_FUCK.getMessage());
    }

    }
    private BotApiMethod<?> handleUpdate(Update update) throws IOException {
            Message message = update.getMessage();
            if (message != null) {
                return messageHandler.answerMessage(update.getMessage());
        }
        return null;
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chatId.toString());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
