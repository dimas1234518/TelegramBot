package com.zhevakin.TelegramBotDemo.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramConfig {

    @Value("${telegram.webhook-path}")
    String webHookPath;

    @Value("${telegram.bot-name}")
    String botName;

    @Value("${telegram.bot-token}")
    String botToken;

}