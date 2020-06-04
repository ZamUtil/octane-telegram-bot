package com.microfocus.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties
public class OctaneUser {
    private String id;
    private String login;
    private String password;
    private List<Object> latestMyWorkList;
    private String name;
    private String firstName;
    private String lastName;

    @JsonProperty("data")
    private void unpackNested(List<Map<String,Object>> data) {
        this.id = (String) data.get(0).get("id");
        this.name = (String) data.get(0).get("name");
        this.firstName = (String) data.get(0).get("first_name");
        this.lastName = (String) data.get(0).get("last_name");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
