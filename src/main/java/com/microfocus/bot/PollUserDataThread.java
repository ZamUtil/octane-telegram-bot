package com.microfocus.bot;

import com.microfocus.bot.http.OctaneAuth;
import com.microfocus.bot.http.OctaneHttpClient;
import com.microfocus.bot.keyboard.KeyboardFactory;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
                //Long userId = Long.valueOf(userData.get(OCTAINE_USER_ID));
                Long userId = 1001L;
                OctaneAuth octaneAuth = new OctaneAuth(userData);

                octaneHttpClient.getNewComments(octaneAuth, userId).stream()
                        .filter(comment -> comment.getWorkItem() != null)//handle only work_item comments
                        .forEach(comment -> {
                            silent.execute(new SendMessage()
                                    .setText(Jsoup.parse(comment.getText()).text())
                                    .setChatId(chatId)
                                    .setReplyMarkup(KeyboardFactory.getCommentInLineButtons(comment.getWorkItem())));
                            octaneHttpClient.markCommentAsRead(octaneAuth, comment.getId());
                        });

                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.debug("stop poll user data");
        }
    }
}
