package com.microfocus.bot;

import com.microfocus.bot.dto.Comment;
import com.microfocus.bot.dto.WorkItem;
import org.jsoup.Jsoup;

public class BotMessageHelper {

    private BotMessageHelper() {
    }

    public static String prepareShotInfo(WorkItem workItem) {
        return getShortTypeName(workItem.getSubtype()) + " " + workItem.getId() + " | " + workItem.getName();
    }

    public static String prepareFullWorkItemInfo(WorkItem workItem) {
        if (workItem.getDescription() == null) {
            return prepareShotInfo(workItem) + "\n -------------- \nDescription: \n -------------- \n" + "IS EMPTY";
        }
        return prepareShotInfo(workItem) + "\n -------------- \nDescription: \n -------------- \n" + Jsoup.parse(workItem.getDescription()).text();
    }

    public static String prepareFormattedMessage(Comment comment) {
        String MESSAGE_TEMPLATE = "%s %s | %s\n -------------- \nAuthor: %s \n -------------- \n %s";
        return String.format(MESSAGE_TEMPLATE,
                getShortTypeName(comment.getOwnerWorkItem().getType()),
                comment.getOwnerWorkItem().getId(),
                comment.getWorkItem().getName(),
                comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName(),
                Jsoup.parse(comment.getText()).text());
    }

    private static String getShortTypeName(String subtype) {
        switch (subtype) {
            case "story":
                return "US";
            case "epic":
                return "E";
            case "defect":
                return "D";
        }
        return subtype;
    }
}
