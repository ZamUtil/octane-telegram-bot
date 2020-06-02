package com.microfocus.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ParrotBot extends TelegramLongPollingBot {

    private static final Logger logger = LogManager.getLogger(ParrotBot.class);

    public void onUpdateReceived(Update update) {
        logger.debug(update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText());
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return Main.BOT_USER_NAME;
    }

    public String getBotToken() {
        return Main.BOT_TOKEN;
    }
}
