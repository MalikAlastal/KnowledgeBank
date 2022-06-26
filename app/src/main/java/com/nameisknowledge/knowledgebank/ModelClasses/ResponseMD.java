package com.nameisknowledge.knowledgebank.ModelClasses;
import java.io.Serializable;

public class ResponseMD implements Serializable {
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
