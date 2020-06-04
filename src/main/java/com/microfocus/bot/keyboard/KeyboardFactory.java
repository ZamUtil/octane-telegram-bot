package com.microfocus.bot.keyboard;

import com.microfocus.bot.Constants;
import com.microfocus.bot.dto.Comment;
import com.microfocus.bot.dto.WorkItem;
import org.apache.commons.lang3.tuple.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory implements Constants {

    public static ReplyKeyboardMarkup getMainBigButtons(boolean withPushButton, boolean withStopPushButton) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(GET_MY_WORK_BIG_BUTTON));
        if (withPushButton) {
            row.add(new KeyboardButton(ENABLE_PUSH_BIG_BUTTON));
        }
        if (withStopPushButton) {
            row.add(new KeyboardButton(DISABLE_PUSH_BIG_BUTTON));
        }
        //row.add(new KeyboardButton(GET_LAST_FAILED_TEST_BIG_BUTTON));
        row.add(new KeyboardButton(LOGOUT_BUTTON_BIG_BUTTON));
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        //keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    public static ReplyKeyboard getLoginInLineButtons() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText(LOGIN_BUTTON).setCallbackData(LOGIN_BUTTON));
        rowsInline.add(rowInline);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

    public static ReplyKeyboard getCommentInLineButtons(Comment.OwnerItem workItem) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText(Constants.REPLY_COMMENT_BUTTON).setCallbackData(getCallbackData(REPLY_COMMENT_BUTTON, workItem)));
        rowInline.add(new InlineKeyboardButton().setText(Constants.VIEW_ITEM_DETAILS_BUTTON).setCallbackData(getCallbackData(VIEW_ITEM_DETAILS_BUTTON, workItem)));
        rowsInline.add(rowInline);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

    public static ReplyKeyboard getWorkItemInLineButtons(WorkItem workItem) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText(Constants.VIEW_ITEM_DETAILS_BUTTON).setCallbackData(getCallbackData(VIEW_ITEM_DETAILS_BUTTON, workItem)));
        rowsInline.add(rowInline);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

    public static ReplyKeyboard getReplyCommentInLineButtons(Pair<Long, String> itemData) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText(Constants.REPLY_COMMENT_BUTTON).setCallbackData(getCallbackData(REPLY_COMMENT_BUTTON, itemData.getLeft(), itemData.getRight())));
        rowsInline.add(rowInline);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

    private static String getCallbackData(String replyCommentButton, Comment.OwnerItem workItem) {
        return getCallbackData(replyCommentButton, workItem.getId(), workItem.getType());
    }

    private static String getCallbackData(String viewItemDetailsButton, WorkItem workItem) {
        return getCallbackData(viewItemDetailsButton, workItem.getId(), workItem.getSubtype());
    }

    private static String getCallbackData(String button, Long workItemId, String workItemType) {
        return button + "{" + workItemId + ":" + workItemType + "}";
    }
}
