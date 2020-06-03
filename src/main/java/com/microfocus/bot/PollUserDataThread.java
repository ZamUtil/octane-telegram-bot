package com.microfocus.bot;

import com.microfocus.bot.http.Comment;
import com.microfocus.bot.http.OctaneAuth;
import com.microfocus.bot.http.OctaneHttpClient;
import com.microfocus.bot.keyboard.KeyboardFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PollUserDataThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PollUserDataThread.class);

    private final Map<String, String> userData;
    private final SilentSender silent;
    private final Long chatId;
    private final OctaneHttpClient octaneHttpClient;

    public PollUserDataThread(Map<String, String> userData, SilentSender silent, Long chatId, OctaneHttpClient octaneHttpClient) {
        this.userData = userData;
        this.silent = silent;
        this.chatId = chatId;
        this.octaneHttpClient = octaneHttpClient;
    }

    public void run() {
        try {
            while (true) {
                logger.debug("poll data for user " + userData.get(Constants.USERNAME_PROP));
                Long userId = 1001L;
                OctaneAuth octaneAuth = new OctaneAuth(userData);

                List<Comment> newComments = octaneHttpClient.getNewComments(octaneAuth, userId);

                for (Comment comment : newComments) {
                    silent.execute(new SendMessage()
                            .setText(comment.getText().replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", ""))
                            .setChatId(chatId)
                            .setReplyMarkup(KeyboardFactory.getCommentInLineButtons()));
                    octaneHttpClient.markCommentAsRead(octaneAuth, comment.getId());
                }

                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.debug("stop poll user data");
        }
    }
}
