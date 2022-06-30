package com.nameisknowledge.knowledgebank.dialogs;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.listeners.GenericListener;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.DialogAreaAttackedBinding;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;

public class TestWinnerDialog extends DialogFragment {

    UserMD winnerName;
    UserMD loserName;
    boolean isWinner ;

    public static final String WINNER_NAME_KEY  =  "WINNER_NAME" ;
    public static final String LOSER_NAME_KEY  =  "LOSER_NAME" ;
    public static final String IS_WINNER_KEY  =  "IS_WINNER_KEY" ;

    DialogAreaAttackedBinding binding ;

    GenericListener<Boolean> listener;

    MediaPlayer glitchSound ;
    MediaPlayer victorySound ;
    MediaPlayer lostSound ;

    boolean isRewardButtonClicked = false ;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (GenericListener<Boolean>) context;
    }

    public static TestWinnerDialog newInstance(UserMD winnerName,String loserName, boolean isWinner) {
        Bundle args = new Bundle();
        TestWinnerDialog fragment = new TestWinnerDialog();
        args.putSerializable(WINNER_NAME_KEY , winnerName);
        args.putSerializable(LOSER_NAME_KEY , loserName);
        args.putBoolean(IS_WINNER_KEY , isWinner);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments() ;
        if (bundle!=null){
            winnerName = (UserMD) bundle.getSerializable(WINNER_NAME_KEY);
            loserName = (UserMD) bundle.getSerializable(LOSER_NAME_KEY);
            isWinner = bundle.getBoolean(IS_WINNER_KEY , false);
        }

        glitchSound = MediaPlayer.create(requireContext() , R.raw.glitchy_sound);
        victorySound = MediaPlayer.create(requireContext() , R.raw.victory);
        lostSound = MediaPlayer.create(requireContext() , R.raw.negative_beeps);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        binding = DialogAreaAttackedBinding.inflate(getLayoutInflater());

//            binding.tvAreaName.setText(area.getAreaName());
//            binding.tvUserUsername.setText(user.getUsername());
//            binding.ivUserImage.setImageResource(Integer.parseInt(UserConstants.getCurrentUser(requireContext()).getAvatarRes()));
//
//            binding.ivKingImage.setImageResource(Integer.parseInt(area.getOwnerUser().getAvatarRes()));
//            binding.tvKingUsername.setText(area.getOwnerUser().getUsername());

        binding.tvEndDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.tvDoubleReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.getData(true);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setView(binding.getRoot());
        builder.setCancelable(false);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isWinner){
                    userWon();
                }
                else {
                    userLost();
                }
            }
        }, DurationConstants.DURATION_SHORT);

        return dialog ;
    }

    private void userWon(){
        glitchSound.start();
        AnimationMethods.flash(DurationConstants.DURATION_SHORT,2 , new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                binding.cardKing.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        glitchSound.stop();
                        victorySound.start();
                        binding.cardUser.setVisibility(View.VISIBLE);
                        binding.tvYou.setTypedText("");
                        AnimationMethods.bounceIn(DurationConstants.DURATION_SHORT, new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                //AnimationMethods.flash(DurationConstants.DURATION_SO_SHORT,0 ,binding.cardUser);
                                binding.tvYou.setTypedText(getString(R.string.you_are_the_king)+"  ");
                            }
                        }, binding.cardUser);
                    }
                }, DurationConstants.DURATION_SO_SHORT);
            }
        }, binding.cardKing);
    }

    private void userLost(){
        binding.tvEndDialog.setText(getString(R.string.ok));
        binding.tvDoubleReward.setEnabled(false);
        lostSound.start();
        AnimationMethods.shake(DurationConstants.DURATION_LONG, new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                binding.tvTheKing.setTypedText(getString(R.string.the_king_is)+" "+winnerName+" ");
            }
        }, binding.cardKing);
    }
}
