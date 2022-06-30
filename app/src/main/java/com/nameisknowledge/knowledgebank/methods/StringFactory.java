package com.nameisknowledge.knowledgebank.methods;

import com.nameisknowledge.knowledgebank.modelClasses.InputsMD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StringFactory {
    private static final char[] letters = {
            'أ', 'ب', 'ت', 'ث', 'ج', 'ح','خ', 'د','ذ','ر','ز','س','ش', 'ص', 'ض', 'ط','ظ','ع', 'غ','ف', 'ق','م','ل','ك','ن', 'ه','و','ي'
    };

    private static final char[] numbers = {
            '0','1','2','3','4','5','6','7','8','9'
    };
    public static char[] makeStringEmpty(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            array[i] = ' ';
        }
        return array;
    }

    public static List<InputsMD> toInputsList(char[] chars){
        List<InputsMD> list = new ArrayList<>();
        for (int i=0;i<chars.length;i++){
            list.add(new InputsMD(chars[i],i));
        }
        return list;
    }

    public static String clearAnswerSpaces(String answer) {
        StringBuilder formatAnswer = new StringBuilder();
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) != ' ') {
                formatAnswer.append(answer.charAt(i));
            }
        }
        return formatAnswer.toString();
    }

    public static List<InputsMD> randomTheAnswer(List<InputsMD> string) {
        InputsMD[] array = string.toArray(new InputsMD[0]);
        for (int i = 0; i < array.length; i++) {
            int randomIndexToSwap = new Random().nextInt(array.length);
            InputsMD temp = array[randomIndexToSwap];
            int index = array[randomIndexToSwap].getIndex();
            array[randomIndexToSwap].setIndex(i);
            array[i].setIndex(index);
            array[randomIndexToSwap] = array[i];
            array[i] = temp;
        }
        return Arrays.asList(array);
    }


    public static List<InputsMD> makeAnswerLonger(String answer) {
        List<InputsMD> list = cutString(answer);
        for (int i = list.size(); i < answer.length() + 4; i++) {
            if (Character.isDigit(answer.charAt(0))){
                list.add(new InputsMD(numbers[new Random().nextInt(numbers.length - 1)],i,true));
            }else {
                list.add(new InputsMD(letters[new Random().nextInt(letters.length - 1)],i,true));
            }
        }
        return randomTheAnswer(list);
    }

    public static List<InputsMD> cutString(String string){
        List<InputsMD> listC = new ArrayList<>();
        for (int i=0;i<string.length();i++) {
            listC.add(new InputsMD(string.charAt(i),i));
        }
        return listC;
    }
}
