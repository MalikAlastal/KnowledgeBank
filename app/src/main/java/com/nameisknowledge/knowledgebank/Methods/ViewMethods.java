package com.nameisknowledge.knowledgebank.Methods;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nameisknowledge.knowledgebank.Constants.DurationConstants;

public class ViewMethods {

    // لإزالة ال view
    public static void goneView(View... views) {
        for (int i = 0 ; i<views.length ; i++){
            views[i].setVisibility(View.GONE);
        }
    }

    // لإظهار ال view
    public static void visibleView(View... views) {
        for (int i = 0 ; i<views.length ; i++){
            views[i].setVisibility(View.VISIBLE);
        }
    }

    // لإخفاء ال view دون إزالتها
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
            }
        }
        return isEmpty ;
    }

    public static boolean isTextInputEmpty(TextInputEditText... editTexts){
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
            AnimationMethods.shake(DurationConstants.DURATION_LONG , edittext);
            showTextInputLayoutError((TextInputEditText) edittext);
        }
    }

    public static void showTextInputLayoutError(TextInputEditText... editTexts){
        for (TextInputEditText editText:editTexts) {
            editText.setError("error");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearTextInputLayoutError(editTexts);
            }
        }, DurationConstants.DURATION_SO_LONG);
    }

    public static void clearTextInputLayoutError(TextInputEditText... editTexts){
        for (TextInputEditText editText:editTexts) {
            editText.setError(null);
        }
    }
}
