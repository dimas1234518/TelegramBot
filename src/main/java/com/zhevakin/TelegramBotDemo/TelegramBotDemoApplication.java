package com.zhevakin.TelegramBotDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBotDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotDemoApplication.class, args);
	}

}
