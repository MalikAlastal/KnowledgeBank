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
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.listeners.GenericListener;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.modelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.DialogAreaAttackedBinding;

public class AreaAttackedDialog extends DialogFragment {

    MapAreaMD area ;
    UserMD user ;
    boolean isWinner ;

    public static final String AREA_KEY  =  "AREA_KEY" ;
    public static final String USER_KEY  =  "USER_KEY" ;
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

    public static AreaAttackedDialog newInstance(MapAreaMD area , UserMD user , boolean isWinner) {
        Bundle args = new Bundle();
        AreaAttackedDialog fragment = new AreaAttackedDialog();
        args.putSerializable(AREA_KEY , area);
        args.putSerializable(USER_KEY , user);
        args.putBoolean(IS_WINNER_KEY , isWinner);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments() ;
        if (bundle!=null){
            area = (MapAreaMD) bundle.getSerializable(AREA_KEY);
            user = (UserMD) bundle.getSerializable(USER_KEY);
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



        try {
            binding.tvAreaName.setText(area.getAreaName());
            binding.tvUserUsername.setText(user.getUsername());
            binding.ivUserImage.setImageResource(Integer.parseInt(UserConstants.getCurrentUser(requireContext()).getAvatarRes()));

            binding.ivKingImage.setImageResource(Integer.parseInt(area.getOwnerUser().getAvatarRes()));
            binding.tvKingUsername.setText(area.getOwnerUser().getUsername());
        }catch (Exception e){

        }
        
        binding.tvEndDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.tvDoubleReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRewardButtonClicked = true ;
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setView(binding.getRoot());
        builder.setCancelable(false);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                listener.getData(isRewardButtonClicked);
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

    @Override
    public void onPause() {
        super.onPause();

        listener.getData(isRewardButtonClicked);
    }

    private void userLost(){
        binding.tvEndDialog.setText(getString(R.string.ok));
        binding.tvDoubleReward.setEnabled(false);
        lostSound.start();
        AnimationMethods.shake(DurationConstants.DURATION_LONG, new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                binding.tvTheKing.setTypedText(getString(R.string.the_king_is)+" "+area.getOwnerUser().getUsername()+" ");
            }
        }, binding.cardKing);
    }
}
