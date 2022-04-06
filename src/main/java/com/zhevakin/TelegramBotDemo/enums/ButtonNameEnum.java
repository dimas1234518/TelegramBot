package com.zhevakin.TelegramBotDemo.enums;

/**
 * Названия кнопок основной клавиатуры
 */
public enum ButtonNameEnum {

    GET_TASKS_BUTTON("Создать файл с заданиями"),
    GET_DICTIONARY_BUTTON("Скачать словарь"),
    UPLOAD_DICTIONARY_BUTTON("Загрузить мой словарь"),
    HELP_BUTTON("Помощь");

    private final String buttonName;

    ButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonName() {
        return buttonName;
    }

}
