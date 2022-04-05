package com.nameisknowledge.knowledgebank.ModelClasses;

public class TestRvMD {
    private char letter;
    private int index;

    public TestRvMD(char letter, int index) {
        this.letter = letter;
        this.index = index;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
