package com.microfocus.bot;

import java.util.HashSet;
import java.util.Set;

public interface Constants {
    //big buttons
    String GET_MY_WORK = "getMyWork";
    String GET_LAST_FAILED_TEST = "getLastFailedTest";
    String LOGOUT_BUTTON = "Logout";

    //line buttons
    String LOGIN_BUTTON = "Login";

    //DB row
    String LOGIN_PROP = "login";
    String PASSWORD_PROP = "password";
    String SING_IN_PROP = "singIn";

    default Set<String> getBigButtons() {
        HashSet<String> bigButtons = new HashSet<>();
        bigButtons.add(GET_MY_WORK);
        bigButtons.add(GET_LAST_FAILED_TEST);
        bigButtons.add(LOGOUT_BUTTON);
        return bigButtons;
    }
}
