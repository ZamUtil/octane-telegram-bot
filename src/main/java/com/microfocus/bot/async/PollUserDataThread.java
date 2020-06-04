package com.microfocus.bot.async;

import com.microfocus.bot.BotMessageHelper;
import com.microfocus.bot.Constants;
import com.microfocus.bot.dto.Comment;
import com.microfocus.bot.dto.MyWorkFollowItem;
import com.microfocus.bot.http.OctaneAuth;
import com.microfocus.bot.http.OctaneHttpClient;
import com.microfocus.bot.keyboard.KeyboardFactory;
import org.apache.commons.collections4.CollectionUtils;
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
                Long userId = Long.valueOf(userData.get(Constants.USER_ID_PROP));
                OctaneAuth octaneAuth = new OctaneAuth(userData);

                handleNewComments(userId, octaneAuth);
                handleNewMyWork(userId, octaneAuth);

                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.debug("stop poll user data");
        }
    }

    private void handleNewComments(Long userId, OctaneAuth octaneAuth) {
        List<Comment> newComments = octaneHttpClient.getNewComments(octaneAuth, userId);
        if (CollectionUtils.isNotEmpty(newComments)) {
            silent.send("New comments:", chatId);
        }
        newComments.stream()
                .filter(comment -> comment.getWorkItem() != null)//handle only work_item comments
                .forEach(comment -> {
                    silent.execute(new SendMessage()
                            .setText(BotMessageHelper.prepareFormattedMessage(comment))
                            .setChatId(chatId)
                            .setReplyMarkup(KeyboardFactory.getCommentInLineButtons(comment.getOwnerWorkItem())));
                });
    }

    private void handleNewMyWork(Long userId, OctaneAuth octaneAuth) {
        List<MyWorkFollowItem> newMyWork = octaneHttpClient.getNewMyWork(octaneAuth, userId);
        if (CollectionUtils.isNotEmpty(newMyWork)) {
            silent.send("New item(s) was assigned to you:", chatId);
        }
        newMyWork.stream()
                .map(MyWorkFollowItem::getWorkItem)
                .forEach(myWorkItem -> {
                    silent.execute(new SendMessage()
                            .setText(BotMessageHelper.prepareShotInfo(myWorkItem))
                            .setChatId(chatId)
                            .setReplyMarkup(KeyboardFactory.getWorkItemInLineButtons(myWorkItem)));
                });
    }
}
