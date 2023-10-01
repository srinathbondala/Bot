package com.example.bot;

public class ModalData {
    private String sender;
    private String message;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModalData(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
}
