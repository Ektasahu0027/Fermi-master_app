package com.example.fermi.fermi.adapter;

import java.util.Date;

/**
 * Created by znt on 9/19/17.
 */

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String msg_Direction = "";

    public ChatMessage(String messageText,String msg_Direction) {
        this.messageText = messageText;
        this.msg_Direction = msg_Direction;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMsgDirection() {
        return msg_Direction;
    }

    public void setMsgDirection(String msg_Direction) {
        this.msg_Direction = msg_Direction;
    }
}