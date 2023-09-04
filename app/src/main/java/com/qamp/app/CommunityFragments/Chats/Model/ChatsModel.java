package com.qamp.app.CommunityFragments.Chats.Model;

public class ChatsModel {

    private String personName;
    private String lastMessage;
    private String time;
    private String messageCount;

    public ChatsModel(String personName, String lastMessage, String time, String messageCount) {
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(String messageCount) {
        this.messageCount = messageCount;
    }
}
