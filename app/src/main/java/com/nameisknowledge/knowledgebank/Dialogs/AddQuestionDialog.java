package com.nameisknowledge.knowledgebank.Dialogs;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.CustomDialogAddQuestionBinding;
import com.nameisknowledge.knowledgebank.databinding.CustomDialogWinnerBinding;

import java.util.List;

public class AddQuestionDialog extends DialogFragment {
    private CustomDialogAddQuestionBinding binding;
    private static GenericListener<List<QuestionMD>> Listener;
    private String gamePlay,senderId;

    public AddQuestionDialog() {
        // Required empty public constructor
    }


    public static AddQuestionDialog newInstance(String senderId,String gamePlay,GenericListener<List<QuestionMD>> genericListener) {
        AddQuestionDialog fragment = new AddQuestionDialog();
        Bundle args = new Bundle();
        Listener = genericListener;
        args.putString("gamePlay",gamePlay);
        args.putString("senderId",senderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            gamePlay = getArguments().getString("gamePlay");
            senderId = getArguments().getString("senderId");
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

//        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String question = binding.edQuestion.getText().toString();
//                String answer = binding.edAnswer.getText().toString();
//                FirebaseFirestore.getInstance().
//                        collection(FirebaseConstants.GAME_PLAY_COLLECTION)
//                        .document(gamePlay).update(
//                        "data."+FirebaseAuth.getInstance().getUid(), FieldValue.arrayUnion(new QuestionMD(question,answer))
//                ).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(requireContext(), "done!", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//            }
//        });

        binding.txtCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                    FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).document(gamePlay)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Listener.getData((List<QuestionMD>) documentSnapshot.get("data."+senderId));

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            }
        });
    }

}
