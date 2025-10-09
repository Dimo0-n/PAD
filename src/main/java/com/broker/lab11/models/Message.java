package com.broker.lab11.models;

public class Message {
    private String type;
    private String body;

    public Message(String type, String body) {
        this.type = type;
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "{ \"type\": \"" + type + "\", \"body\": \"" + body + "\" }";
    }
}
