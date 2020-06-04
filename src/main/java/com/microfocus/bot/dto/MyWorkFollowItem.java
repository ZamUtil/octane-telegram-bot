package com.microfocus.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class MyWorkFollowItem {

    private Long id;
    private String type;
    @JsonProperty("my_follow_items_work_item")
    private WorkItem workItem;
    private OctaneUser author;
        public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }

    public OctaneUser getAuthor() {
        return author;
    }

    public void setAuthor(OctaneUser author) {
        this.author = author;
    }
}
