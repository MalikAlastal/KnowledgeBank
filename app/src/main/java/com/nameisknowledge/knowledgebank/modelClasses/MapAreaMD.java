package com.nameisknowledge.knowledgebank.modelClasses;

import com.nameisknowledge.knowledgebank.modelClasses.questions.MapFireBaseQuestionMD;

import java.io.Serializable;
import java.util.List;

public class MapAreaMD implements Serializable {
    String areaName ="";
    double areaLng =0;
    double areaLat =0;
    UserMD ownerUser ;
    int ownerAnsweredQuestionsCount =0 ;

    List<MapFireBaseQuestionMD> questionList ;

    public String getAreaName() {
        return areaName;
    }

    public MapAreaMD() {
    }

    public MapAreaMD(String areaName, double areaLng, double areaLat, UserMD ownerUser, int ownerAnsweredQuestionsCount, List<MapFireBaseQuestionMD> questionList) {
        this.areaName = areaName;
        this.areaLng = areaLng;
        this.areaLat = areaLat;
        this.ownerUser = ownerUser;
        this.ownerAnsweredQuestionsCount = ownerAnsweredQuestionsCount;
        this.questionList = questionList;
    }

    public int getOwnerAnsweredQuestionsCount() {
        return ownerAnsweredQuestionsCount;
    }

    public void setOwnerAnsweredQuestionsCount(int ownerAnsweredQuestionsCount) {
        this.ownerAnsweredQuestionsCount = ownerAnsweredQuestionsCount;
    }

    public UserMD getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(UserMD ownerUser) {
        this.ownerUser = ownerUser;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public double getAreaLng() {
        return areaLng;
    }

    public void setAreaLng(double areaLng) {
        this.areaLng = areaLng;
    }

    public double getAreaLat() {
        return areaLat;
    }

    public void setAreaLat(double areaLat) {
        this.areaLat = areaLat;
    }

    public List<MapFireBaseQuestionMD> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<MapFireBaseQuestionMD> questionList) {
        this.questionList = questionList;
    }
}
