package com.nameisknowledge.knowledgebank.Activities;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nameisknowledge.knowledgebank.Adapters.TestRvAdapter;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.ModelClasses.MapQuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.TestRvMD;
import com.nameisknowledge.knowledgebank.databinding.ActivityAttackAreaBinding;

import java.util.List;
import java.util.Random;

public class AttackAreaActivity extends AppCompatActivity {

    ActivityAttackAreaBinding binding ;

    public static final String AREA_KEY = "AREA_KEY";

    private MapAreaMD area ;
    ToastMethods toastMethods ;

    List<MapQuestionMD> questionList ;
    TestRvAdapter inputAdapter ;
    TestRvAdapter answerAdapter ;

    public final String[] letters = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttackAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prepareActivity();
    }

    private void prepareActivity(){
        area = (MapAreaMD) getIntent().getSerializableExtra(AREA_KEY);
        toastMethods = new ToastMethods(this);

        toastMethods.info(area.getAreaName());
        questionList = area.getQuestionList();

        binding.tvQuestion.setText(questionList.get(0).getAnswer());
        binding.tvAreaName.setText(area.getAreaName());

        for (MapQuestionMD question: questionList) {
            question.setAnswer(clearAnswerSpaces(question.getAnswer()));
        }

        prepareRecyclers();
    }

    private void prepareRecyclers(){

        inputAdapter = new TestRvAdapter(updateInput(questionList.get(0).getAnswer()), true, new GenericListener<TestRvMD>() {
            @Override
            public void getData(TestRvMD testRvMD) {
                answerAdapter.checkEmpty(new GenericListener<List<Integer>>() {
                    @Override
                    public void getData(List<Integer> integers) {
                        if (integers.size() !=0){
                            inputAdapter.setEmpty(testRvMD.getIndex(), testRvMD);
                            answerAdapter.addChar(testRvMD);
                        }

                        submit(mergeAnswerChars(answerAdapter.getMyList()));
                    }
                });
            }
        });

       answerAdapter = new TestRvAdapter(makeStringEmpty(questionList.get(0).getAnswer()), false, new GenericListener<TestRvMD>() {
            @Override
            public void getData(TestRvMD testRvMD) {
                inputAdapter.setChar(testRvMD);
            }
        });

        ViewMethods.prepareRecycler(binding.rvAnswer , answerAdapter  , true , new GridLayoutManager(this , 5));
        ViewMethods.prepareRecycler(binding.rvInput , inputAdapter  , true , new GridLayoutManager(this , 5));

    }

    private String makeStringEmpty(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            array[i] = ' ';
        }
        return String.valueOf(array);
    }

    private String updateInput(String answer) {
        StringBuilder finalAnswer = new StringBuilder();
        finalAnswer.append(answer);
        for (int i = answer.length(); i < answer.length() + 4; i++) {
            finalAnswer.append(letters[new Random().nextInt(((letters.length - 1)) + 1)]);
        }
        return randomTheAnswer(finalAnswer.toString());
    }

    private String randomTheAnswer(String string) {
        char[] array = string.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int randomIndexToSwap = new Random().nextInt(array.length);
            char temp = array[randomIndexToSwap];
            array[randomIndexToSwap] = array[i];
            array[i] = temp;
        }
        return String.valueOf(array);
    }

    private String clearAnswerSpaces(String answer){
        StringBuilder formatAnswer = new StringBuilder();
        for (int i = 0 ; i<answer.length() ; i++) {
            if (answer.charAt(i)!=' '){
                formatAnswer.append(answer.charAt(i));
            }
        }
        return formatAnswer.toString() ;
    }

    private void submit(String answer) {
        if (TextUtils.equals(answer, questionList.get(0).getAnswer())) {
            toastMethods.success("Fu toxic");
        }
    }

    private String mergeAnswerChars(List<TestRvMD> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i).getLetter());
        }
        return stringBuilder.toString();
    }
}