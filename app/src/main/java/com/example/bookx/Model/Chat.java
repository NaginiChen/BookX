package com.example.bookx.Model;

public class Chat {
    private String sender, receiver, message ;
    private boolean isRead, isSent;

    public Chat(String sender, String receiver, String message, boolean isRead, boolean isSent) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isRead = isRead;
        this.isSent = isSent;
    }

    public Chat(){

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isSent(){ return isSent; }

    public void setSent(boolean sent){ isSent = sent; }

}
