package com.microfocus.bot;

import java.util.HashSet;
import java.util.Set;

public interface Constants {
    //big buttons
    String GET_MY_WORK_BIG_BUTTON = "getMyWork";
    String GET_LAST_FAILED_TEST_BIG_BUTTON = "getLastFailedTest";
    String LOGOUT_BUTTON_BIG_BUTTON = "Logout";

    //line buttons
    String LOGIN_BUTTON = "Login";
    String REPLY_COMMENT_BUTTON = "Reply Comment";
    String VIEW_ITEM_DETAILS_BUTTON = "View Comment details";

    //reply messages
    String PLEASE_PROVIDE_LOGIN_REPLY = "Please provide login";
    String PLEASE_PROVIDE_PASSWORD_REPLY = "Please provide password";

    //DB row
    String USERNAME_PROP = "username";
    String PASSWORD_PROP = "password";
    String SING_IN_PROP = "singIn";

    default Set<String> getBigButtons() {
        HashSet<String> bigButtons = new HashSet<>();
        bigButtons.add(GET_MY_WORK_BIG_BUTTON);
        bigButtons.add(GET_LAST_FAILED_TEST_BIG_BUTTON);
        bigButtons.add(LOGOUT_BUTTON_BIG_BUTTON);
        return bigButtons;
    }
}
