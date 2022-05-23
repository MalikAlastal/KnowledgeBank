package com.nameisknowledge.knowledgebank.ModelClasses;

import java.util.List;

public class MapAreaMD {
    String areaName ="";
    double areaLng =0;
    double areaLat =0;


    List<MapQuestion> questionList ;

    public String getAreaName() {
        return areaName;
    }

    public MapAreaMD() {
    }

    public MapAreaMD(String areaName, double areaLng, double areaLat, List<MapQuestion> questionList) {
        this.areaName = areaName;
        this.areaLng = areaLng;
        this.areaLat = areaLat;
        this.questionList = questionList;
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

    public List<MapQuestion> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<MapQuestion> questionList) {
        this.questionList = questionList;
    }
}
