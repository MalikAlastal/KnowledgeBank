package com.nameisknowledge.knowledgebank.Methods;

import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class AnimationMethods {

    public static void slideOutRight(int duration , View... views){
        for (View view :views) {
            YoYo.with(Techniques.SlideOutRight).duration(duration).playOn(view);
        }
    }

    public static void slideInRight(int duration , View... views){
        for (View view :views) {
            YoYo.with(Techniques.SlideInRight).duration(duration).playOn(view);
        }
    }

    public static void flipInX(int duration , View... views){
        for (View view :views) {
            YoYo.with(Techniques.FlipInX).duration(duration).playOn(view);
        }
    }

    public static void flipOutX(int duration , View... views){
        for (View view :views) {
            YoYo.with(Techniques.FlipOutX).duration(duration).playOn(view);
        }
    }

    public static void flipOutX(int duration , YoYo.AnimatorCallback callback, View... views){
        for (View view :views) {
            YoYo.with(Techniques.FlipOutX).duration(duration).onEnd(callback).playOn(view);
        }
    }

    public static void bounceInDown(int duration , View... views){
        for (View view :views) {
            YoYo.with(Techniques.BounceInDown).duration(duration).playOn(view);
        }
    }

    public static void bounceInUp(int duration , View... views){
        for (View view :views) {
            YoYo.with(Techniques.BounceInUp).duration(duration).playOn(view);
        }
    }

    public static void bounce(int duration , View... views){
        for (View view :views) {
            YoYo.with(Techniques.Shake).duration(duration).playOn(view);
        }
    }

}
