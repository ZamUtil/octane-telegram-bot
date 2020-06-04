package com.microfocus.bot;

import com.microfocus.bot.dto.Comment;
import com.microfocus.bot.dto.WorkItem;
import org.jsoup.Jsoup;

public class BotMessageHelper {

    private BotMessageHelper() {
    }

    public static String prepareShotInfo(WorkItem workItem) {
        return workItem.getSubtype() + " " + workItem.getId() + " | " + workItem.getName();
    }

    public static String prepareFullWorkItemInfo(WorkItem workItem) {
        if (workItem.getDescription() == null) {
            return prepareShotInfo(workItem) + "\n" + "Description: \n" + "IS EMPTY";
        }
        return prepareShotInfo(workItem) + "\n" + "Description: \n" + Jsoup.parse(workItem.getDescription()).text();
    }

    public static String prepareFormattedMessage(Comment comment) {
        String MESSAGE_TEMPLATE = "%s %s | %s\n Author: %s\n ==============\n %s";
        return String.format(MESSAGE_TEMPLATE,
                comment.getOwnerWorkItem().getShortTypeName(),
                comment.getOwnerWorkItem().getId(),
                comment.getWorkItem().getName(),
                comment.getAuthor().getName(),
                Jsoup.parse(comment.getText()).text());
    }
}
