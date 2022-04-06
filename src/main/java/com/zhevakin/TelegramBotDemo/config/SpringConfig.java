package com.zhevakin.TelegramBotDemo.config;

import com.zhevakin.TelegramBotDemo.handlers.MessageHandler;
import com.zhevakin.TelegramBotDemo.model.WriteReadBot;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@AllArgsConstructor
public class SpringConfig {

    private final TelegramConfig telegramConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramConfig.getWebHookPath()).build();
    }

    @Bean
    public WriteReadBot springWebhookBot(SetWebhook setWebhook, MessageHandler messageHandler) {
        WriteReadBot bot = new WriteReadBot(setWebhook, messageHandler);
        bot.setBotPath(telegramConfig.getWebHookPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setBotToken(telegramConfig.getBotToken());
        return bot;
    }

}
