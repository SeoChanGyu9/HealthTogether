package com.example.firebase_test;

public class Message {
    public static String SENT_BY_ME = "나";
    public static String SENT_BY_BOT = "봇";
    String message;
    String sentBy;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }
}
