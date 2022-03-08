package com.nameisknowledge.knowledgebank.Methods;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class ViewMethods {

    // لإزالة الview
    public static void goneView(View... views) {
        for (int i = 0 ; i<views.length ; i++){
            views[i].setVisibility(View.GONE);
        }
    }

    // لإظهار الview
    public static void visibleView(View... views) {
        for (int i = 0 ; i<views.length ; i++){
            views[i].setVisibility(View.VISIBLE);
        }
    }

    // لإخفاء الview دون إزالتها
    public static void invisibleView(View... views) {
        for (int i = 0 ; i<views.length ; i++){
            views[i].setVisibility(View.INVISIBLE);
        }
    }

    // لتعطيل أو تفعيل ال view القمية المنطقية المعطاة هي التي تحدد
    public static void enableView(boolean state , View... views){
        for (int i =0 ; i< views.length ; i++){
            views[i].setEnabled(state);
        }
    }

    public static boolean isEditTextEmpty(EditText... editTexts){
        boolean isEmpty = false ;

        for (int i =0 ; i< editTexts.length ; i++){
            if (TextUtils.isEmpty(editTexts[i].getText().toString())){
                isEmpty = true ;
                editTextEmptyError(editTexts[i]);
            }
        }
        return isEmpty ;
    }


    public static void editTextEmptyError(EditText... editTexts){
        for (EditText edittext:editTexts) {
            AnimationMethods.bounce(1500 , edittext);
        }
    }

    public static void showTextInputLayoutError(TextInputLayout... layouts){
        for (TextInputLayout layout:layouts) {
            layout.setError("error");
        }
    }

    public static void clearTextInputLayoutError(TextInputLayout... layouts){
        for (TextInputLayout layout:layouts) {
            layout.setError("");
        }
    }
}
