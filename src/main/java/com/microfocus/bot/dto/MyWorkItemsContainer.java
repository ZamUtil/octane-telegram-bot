package com.microfocus.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyWorkItemsContainer {
    @JsonProperty("total_count")
    private Integer count;

    private List<MyWorkFollowItem> data;

    public List<MyWorkFollowItem> getData() {
        return data;
    }

    public void setData(List<MyWorkFollowItem> data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
