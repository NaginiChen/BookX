package com.example.bookx.Model;

// class model for chat messages
public class Chat {

    // sender and receiver id
    private String sender, receiver ;
    // message content
    private String message ;
    // if the message is sent by sender and read by receiver
    private boolean isRead, isSent;

    public Chat(String sender, String receiver, String message, boolean isRead, boolean isSent) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isRead = isRead;
        this.isSent = isSent;
    }

    public Chat(){
        // Default constructor required for calls to DataSnapshot.getValue(Chat.class)
    }

    // setters & getters
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
