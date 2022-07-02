package com.nameisknowledge.knowledgebank.modelClasses;

import java.io.Serializable;

public class InputsMD implements Serializable {
    private char letter;
    private int index;
    private boolean isAdded,isShown;

    public InputsMD(char letter, int index) {
        this.letter = letter;
        this.index = index;
        this.isAdded = false;
        this.isShown = false;
    }

    public InputsMD(char letter, int index,boolean isAdded) {
        this.letter = letter;
        this.index = index;
        this.isAdded = isAdded;
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

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public boolean isShown() {
        return isShown;
    }

    public InputsMD setShown(boolean shown) {
        isShown = shown;
        return this;
    }
}
