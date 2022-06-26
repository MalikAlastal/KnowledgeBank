package com.nameisknowledge.knowledgebank.ModelClasses;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Map;

public class ResponseMD implements Serializable {
    private String roomID,senderName;
    public ResponseMD(String roomID,String senderName) {
        this.roomID = roomID;
        this.senderName = senderName;
    }

    public ResponseMD() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String userID) {
        this.senderName = userID;
    }
}
