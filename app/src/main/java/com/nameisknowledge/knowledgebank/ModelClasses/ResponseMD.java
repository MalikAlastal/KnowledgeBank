package com.nameisknowledge.knowledgebank.ModelClasses;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Map;

public class ResponseMD implements Serializable {
    private String roomID,userID;
    public ResponseMD(String roomID,String userID) {
        this.roomID = roomID;
        this.userID = userID;
    }

    public ResponseMD() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
