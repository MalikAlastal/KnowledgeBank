package com.nameisknowledge.knowledgebank.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.modelClasses.questions.LocalQuestionMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.CustomDialogAddQuestionBinding;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionDialog extends DialogFragment {
    private CustomDialogAddQuestionBinding binding;
    private String roomID;
    private final List<LocalQuestionMD> questions = new ArrayList<>();
    private int questionCount;

    public AddQuestionDialog() {
        // Required empty public constructor
    }


    public static AddQuestionDialog newInstance(String roomID) {
        AddQuestionDialog fragment = new AddQuestionDialog();
        Bundle args = new Bundle();
        args.putString("roomID",roomID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            roomID = getArguments().getString("roomID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.custom_dialog_add_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = CustomDialogAddQuestionBinding.bind(view);

        binding.btnAdd.setOnClickListener(view1 -> {
            String question = binding.edQuestion.getText().toString();
            String answer = binding.edAnswer.getText().toString();
            questions.add(new LocalQuestionMD(question,answer));
            questionCount++;
            if (questionCount == 3){
                done();
            }
        });
    }

    private void done(){
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.GAME_PLAY_2_COLLECTION)
                .document(roomID)
                .update("questions"+"."+ UserConstants.getCurrentUser(requireContext()).getUid(),questions)
                .addOnSuccessListener(unused -> {
                        FirebaseFirestore.getInstance()
                                .collection(FirebaseConstants.GAME_PLAY_2_COLLECTION)
                                .document(roomID)
                                .update("isQuestionsAdded",FieldValue.increment(1));
                        dismiss();

                })
                .addOnFailureListener(e->{
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
