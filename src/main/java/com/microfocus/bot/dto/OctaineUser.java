package com.microfocus.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties
public class OctaineUser {
    private String id;
    private String login;
    private String password;
    private List<Object> latestMyWorkList;

    @JsonProperty("data")
    private void unpackNested(List<Map<String,Object>> data) {
        this.id = (String) data.get(0).get("id");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Object> getLatestMyWorkList() {
        return latestMyWorkList;
    }

    public void setLatestMyWorkList(List<Object> latestMyWorkList) {
        this.latestMyWorkList = latestMyWorkList;
    }
}
