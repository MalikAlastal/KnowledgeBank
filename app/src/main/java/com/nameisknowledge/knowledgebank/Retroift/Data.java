package com.nameisknowledge.knowledgebank.Retroift;

import java.io.Serializable;

public class Data implements Serializable {
    private String name,age;

    public Data(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public Data() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
