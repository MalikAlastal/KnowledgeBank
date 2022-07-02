package com.nameisknowledge.knowledgebank.modelClasses;
import java.io.Serializable;

public class ResponseMD implements Serializable {
    private String roomID,mode;
    public ResponseMD(String roomID,String mode) {
        this.roomID = roomID;
        this.mode = mode;
    }

    public ResponseMD() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
