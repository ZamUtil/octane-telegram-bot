package com.microfocus.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {

    public static final String BOT_USER_NAME = "octane_hackathon2020_bot";
    public static final String BOT_TOKEN = "1094483244:AAFlGShQHGuSLcV8SejjSXesuW27lC1k1CQ";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new ParrotBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
