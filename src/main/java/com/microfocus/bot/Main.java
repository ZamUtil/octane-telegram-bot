package com.microfocus.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static final String BOT_USER_NAME = "octane_hackathon2020_bot";
    public static final String BOT_TOKEN = "1094483244:AAFlGShQHGuSLcV8SejjSXesuW27lC1k1CQ";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            //telegramBotsApi.registerBot(new ParrotBot());
            //telegramBotsApi.registerBot(new AbilityBotTest(BOT_TOKEN, BOT_USER_NAME));
            telegramBotsApi.registerBot(new OctaneBot(BOT_TOKEN, BOT_USER_NAME));
        } catch (TelegramApiRequestException e) {
            logger.error("Oops, something went wrong while registering bot", e);
        }
    }
}
