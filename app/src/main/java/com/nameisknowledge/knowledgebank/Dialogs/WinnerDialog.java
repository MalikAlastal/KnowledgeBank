package com.nameisknowledge.knowledgebank.Dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.CustomDialogWinnerBinding;

public class WinnerDialog extends DialogFragment {
    private CustomDialogWinnerBinding binding;
    private String name;
    public WinnerDialog() {
        // Required empty public constructor
    }


    public static WinnerDialog newInstance(String name) {
        WinnerDialog fragment = new WinnerDialog();
        Bundle args = new Bundle();
        args.putString("winnerName",name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            name = getArguments().getString("winnerName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.custom_dialog_winner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = CustomDialogWinnerBinding.bind(view);
        binding.tvWinnerName.setText("The Winner is :"+name);
    }
}
