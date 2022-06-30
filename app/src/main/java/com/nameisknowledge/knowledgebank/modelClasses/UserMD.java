package com.nameisknowledge.knowledgebank.modelClasses;

import java.io.Serializable;
import java.util.Date;

public class UserMD implements Serializable {

    private String uid , username , email , password , gender , avatarRes ,notificationToken;
    private Date birthdate , creationDate ;
    int areaAttackPoints ;

    public UserMD(String uid, String username, String email, String password, String gender, String avatarRes, String notificationToken, Date birthdate, Date creationDate, int areaAttackPoints) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.avatarRes = avatarRes;
        this.notificationToken = notificationToken;
        this.birthdate = birthdate;
        this.creationDate = creationDate;
        this.areaAttackPoints = areaAttackPoints;
    }




    public UserMD() {
    }

    public int getAreaAttackPoints() {
        return areaAttackPoints;
    }

    public void setAreaAttackPoints(int areaAttackPoints) {
        this.areaAttackPoints = areaAttackPoints;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarRes() {
        return avatarRes;
    }

    public void setAvatarRes(String avatarRes) {
        this.avatarRes = avatarRes;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }
}
