package com.microfocus.bot;

import java.util.HashSet;
import java.util.Set;

public interface Constants {
    //userId
    Long USER_ID = 1001l;

    //big buttons
    String GET_MY_WORK_BIG_BUTTON = "Get My Work";
    String DISABLE_PUSH_BIG_BUTTON = "Disable Push Notifications";
    String ENABLE_PUSH_BIG_BUTTON = "Enable Push Notifications";
    String LOGOUT_BUTTON_BIG_BUTTON = "Logout";

    //line buttons
    String LOGIN_BUTTON = "Login";
    String REPLY_COMMENT_BUTTON = "Reply Comment";
    String VIEW_ITEM_DETAILS_BUTTON = "View Item details";

    //reply messages
    String PLEASE_PROVIDE_LOGIN_REPLY = "Please Enter your Login to Octane Example of the Format - username@microfocus.com or just username";
    String PLEASE_PROVIDE_PASSWORD_REPLY = "Please Enter your password";
    String PLEASE_PROVIDE_REPLY_MESSAGE_REPLY = "Please write your respond";

    //DB row
    String USERNAME_PROP = "username";
    String PASSWORD_PROP = "password";
    String SING_IN_PROP = "singIn";
    String OCTANE_USER_ID = "octaneUserId";
    String LAST_REPLY_COMMENT_ITEM_ID = "lastReplyCommentItemId";
    String LAST_REPLY_COMMENT_ITEM_TYPE = "lastReplyCommentItemType";

    default Set<String> getBigButtons() {
        HashSet<String> bigButtons = new HashSet<>();
        bigButtons.add(GET_MY_WORK_BIG_BUTTON);
        bigButtons.add(DISABLE_PUSH_BIG_BUTTON);
        bigButtons.add(ENABLE_PUSH_BIG_BUTTON);
        bigButtons.add(LOGOUT_BUTTON_BIG_BUTTON);
        return bigButtons;
    }
}
