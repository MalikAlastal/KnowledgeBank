package com.nameisknowledge.knowledgebank.ModelClasses;

public class ResponseMD {
    private String roomID;

    public ResponseMD(String roomID) {
        this.roomID = roomID;
    }

    public ResponseMD() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
