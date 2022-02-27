package com.nameisknowledge.knowledgebank;

public class MD_Question {
    private int index;
    private String question,answer;

    public MD_Question(int index, String question, String answer) {
        this.index = index;
        this.question = question;
        this.answer = answer;
    }

    public MD_Question() {
    }

    public int getIndex() {
        return index;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
