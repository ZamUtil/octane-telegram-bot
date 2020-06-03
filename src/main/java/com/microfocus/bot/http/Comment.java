package com.microfocus.bot.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    private Long id;

    private String text;

    @JsonProperty("owner_work_item")
    private OwnerItem workItem;

    @JsonProperty("owner_run")
    private OwnerItem run;

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

    public OwnerItem getRun() {
        return run;
    }

    public void setRun(OwnerItem run) {
        this.run = run;
    }

    static class OwnerItem {
        private Long id;
        private String type;

        public OwnerItem() {
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


