package com.nameisknowledge.knowledgebank.ModelClasses;

import java.io.Serializable;

public class InputsMD implements Serializable {
    private char letter;
    private int index;

    public InputsMD(char letter, int index) {
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
