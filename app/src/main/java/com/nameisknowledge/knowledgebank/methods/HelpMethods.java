package com.nameisknowledge.knowledgebank.methods;

import static com.nameisknowledge.knowledgebank.methods.StringFactory.cutString;

import com.nameisknowledge.knowledgebank.adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.listeners.GenericListener;
import com.nameisknowledge.knowledgebank.modelClasses.InputsMD;

import java.util.ArrayList;
import java.util.List;

public class HelpMethods {

    private static List<Integer> repeatedCharsIndexes = new ArrayList<>();
    private static int repeatedCharsCurrentIndex,deletedChars,shownChars;

    public static void emptyValues(){
        repeatedCharsIndexes = new ArrayList<>();
        repeatedCharsCurrentIndex = 0;
        shownChars = 0;
        deletedChars = 0;
    }

    public static void deleteChar(int points,GamePlayAdapter inputAdapter,GenericListener<String> listener) {
        if (deletedChars == 5 || points < 1){
            listener.getData("لقد نفذت مساعداتك");
        }else {
            deletedChars++;
            for (InputsMD inputsMD : inputAdapter.getMyList()) {
                if (inputsMD.isAdded()) {
                    inputAdapter.setEmpty(inputsMD.getIndex(), inputsMD);
                    break;
                }
            }
            listener.getData("");
        }
    }

    public static void showHint(int points,String hint,GenericListener<String> listener){
        if (points < 5){
            listener.getData("لقد نفذت مساعداتك");
        }else {
            listener.getData(hint);
        }
    }

    public static void showChar(int points,GamePlayAdapter inputAdapter, GamePlayAdapter answerAdapter, String currentQuestionAnswer,GenericListener<String> listener) {
        if (shownChars == 4 || points < 2){
            listener.getData("لقد نفذت مساعداتك");
        }else {
            shownChars++;
            for (int i = 0; i < inputAdapter.getMyList().size(); i++) {
                // to check if the char is NotAdded;
                if (!inputAdapter.getMyList().get(i).isAdded() && inputAdapter.getMyList().get(i).getLetter() != ' ') {
                    // to check if the char is exists in real answer (answerArray)
                    if (toCharList(answerAdapter.getMyList()).contains(inputAdapter.getMyList().get(i).getLetter())) {
                        // if If it is twice in the answer
                        if (getCountOfChar(inputAdapter.getMyList().get(i).getLetter(), currentQuestionAnswer) > 1) {
                            // then get the last index
                            int index = repeatedCharsIndexes.get(repeatedCharsCurrentIndex);
                            inputAdapter.setEmpty(inputAdapter.getMyList().get(i).getIndex(), inputAdapter.getMyList().get(i));
                            answerAdapter.setChar(new InputsMD(currentQuestionAnswer.charAt(index), index).setShown(true));
                            repeatedCharsCurrentIndex++;
                        }
                    } else {
                        // then get the first index
                        int index = toCharList(cutString(currentQuestionAnswer)).indexOf(inputAdapter.getMyList().get(i).getLetter());
                        inputAdapter.setEmpty(inputAdapter.getMyList().get(i).getIndex(), inputAdapter.getMyList().get(i));
                        answerAdapter.setChar(new InputsMD(currentQuestionAnswer.charAt(index), index).setShown(true));
                    }
                    break;
                }
            }
            listener.getData("");
        }
    }

    private static int getCountOfChar(char chr,String currentQuestionAnswer) {
        int count = 0;
        for (int i = 0; i < currentQuestionAnswer.length(); i++) {
            if (currentQuestionAnswer.charAt(i) == chr) {
                if (count > 0) {
                    repeatedCharsIndexes.add(i);
                }
                count++;
            }
        }
        return count;
    }

    private static List<Character> toCharList(List<InputsMD> list) {
        List<Character> characterList = new ArrayList<>();
        for (InputsMD inputsMD : list) {
            characterList.add(inputsMD.getLetter());
        }
        return characterList;
    }
}
