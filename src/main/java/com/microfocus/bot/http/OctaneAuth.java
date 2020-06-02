package com.microfocus.bot.http;

import com.microfocus.bot.Constants;

import java.util.Map;

public class OctaneAuth {

    private String user;
    private String password;

    public OctaneAuth() {
    }

    public OctaneAuth(Map<String, String> userDB) {
        this.user = userDB.get(Constants.USERNAME_PROP);
        this.password = userDB.get(Constants.PASSWORD_PROP);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
