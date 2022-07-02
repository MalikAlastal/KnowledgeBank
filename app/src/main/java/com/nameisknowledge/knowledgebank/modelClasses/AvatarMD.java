package com.nameisknowledge.knowledgebank.modelClasses;

public class AvatarMD {

    private int avatarRes ;
    private String avatarGender ;
    private String avatarTitle ;

    public AvatarMD(int avatarRes, String avatarGender, String avatarTitle) {
        this.avatarRes = avatarRes;
        this.avatarGender = avatarGender;
        this.avatarTitle = avatarTitle;
    }

    public AvatarMD() {
    }

    public int getAvatarRes() {
        return avatarRes;
    }

    public void setAvatarRes(int avatarRes) {
        this.avatarRes = avatarRes;
    }

    public String getAvatarGender() {
        return avatarGender;
    }

    public void setAvatarGender(String avatarGender) {
        this.avatarGender = avatarGender;
    }

    public String getAvatarTitle() {
        return avatarTitle;
    }

    public void setAvatarTitle(String avatarTitle) {
        this.avatarTitle = avatarTitle;
    }
}
