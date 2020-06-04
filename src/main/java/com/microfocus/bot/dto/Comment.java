package com.microfocus.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {
    private Long id;

    private String text;

    @JsonProperty("owner_work_item")
    private OwnerItem ownerWorkItem;

    private WorkItem workItem;

    private OctaneUser author;

    public Comment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OwnerItem getOwnerWorkItem() {
        return ownerWorkItem;
    }

    public void setOwnerWorkItem(OwnerItem ownerWorkItem) {
        this.ownerWorkItem = ownerWorkItem;
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

    public static class OwnerItem {
        private Long id;
        private String type;

        public OwnerItem() {
        }

        public OwnerItem(Long id, String type) {
            this.id = id;
            this.type = type;
        }

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
    }
}


