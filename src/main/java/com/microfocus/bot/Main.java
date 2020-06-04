package com.microfocus.bot;

import com.microfocus.bot.async.PollUserDataThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(PollUserDataThread.class);

    public static final String BOT_USER_NAME = "";
    public static final String BOT_TOKEN = "";

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
