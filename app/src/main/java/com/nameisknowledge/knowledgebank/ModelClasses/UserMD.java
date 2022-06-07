package com.nameisknowledge.knowledgebank.ModelClasses;

import java.io.Serializable;
import java.util.Date;

public class UserMD implements Serializable {

    private String uid , username , email , password , gender , avatarRes ,notificationToken;
    private Date birthdate , creationDate ;

    public UserMD(String username, String email, String password, String gender, String avatarRes, Date birthdate, Date creationDate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.avatarRes = avatarRes;
        this.birthdate = birthdate;
        this.creationDate = creationDate;
    }

    public UserMD(String uid, String username, String email, String password, String gender, String avatarRes, Date birthdate, Date creationDate) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.avatarRes = avatarRes;
        this.birthdate = birthdate;
        this.creationDate = creationDate;
    }

    public UserMD() {
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
