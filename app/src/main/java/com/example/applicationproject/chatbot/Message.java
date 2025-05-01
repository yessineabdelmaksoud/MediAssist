package com.example.applicationproject.chatbot;

public class Message {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;

    private String content;
    private int type;
    private long timestamp;

    public Message(String content, int type) {
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}