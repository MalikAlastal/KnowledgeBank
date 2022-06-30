package com.nameisknowledge.knowledgebank.modelClasses;

public class ModeMD {

    int resName;
    int resImage;
    int resMainColor;

    public ModeMD(int resName, int resImage, int mainColor) {
        this.resName = resName;
        this.resImage = resImage;
        this.resMainColor = mainColor;
    }

    public ModeMD() {
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResImage() {
        return resImage;
    }

    public void setResImage(int resImage) {
        this.resImage = resImage;
    }

    public int getResMainColor() {
        return resMainColor;
    }

    public void setResMainColor(int resMainColor) {
        this.resMainColor = resMainColor;
    }
}