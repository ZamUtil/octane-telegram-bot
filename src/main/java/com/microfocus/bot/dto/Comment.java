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
    private OwnerItem workItem;

/*    @JsonProperty("owner_run")
    private OwnerItem run;*/

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

    public OwnerItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(OwnerItem workItem) {
        this.workItem = workItem;
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


