package com.nameisknowledge.knowledgebank.ModelClasses;

public class ResponseMD {
    private String roomID,senderID;
    public ResponseMD(String roomID,String senderID) {
        this.roomID = roomID;
        this.senderID = roomID;
    }

    public ResponseMD() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getSenderId() {
        return senderID;
    }

    public void setSenderId(String senderId) {
        this.senderID = senderId;
    }
}
