package com.example.ecommerce.domain;

public class Notification {
    private String key;
    private String message;
    private String timestamp;
    private Boolean seen;

    public Notification() {}

    public Notification( String message, String timestamp, Boolean seen) {
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public Notification(String key, String message, String timestamp, Boolean seen) {
        this.key = key;
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
