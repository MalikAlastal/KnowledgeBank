package com.nameisknowledge.knowledgebank.methods;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;

public class ViewMethods {

    // لإزالة ال view
    public static void goneView(View... views) {
        for (int i = 0 ; i<views.length ; i++){
            views[i].setVisibility(View.GONE);
        }
    }

    public static String getText(EditText editText){
        return editText.getText().toString();
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
            showTextInputError((TextInputEditText) edittext);
        }
    }

    public static void showTextInputError(TextInputEditText... editTexts){
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

    public static <T extends RecyclerView.ViewHolder> void  prepareRecycler(RecyclerView recycler , RecyclerView.Adapter<T> adapter , boolean hasFixedSize , RecyclerView.LayoutManager manager){
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(hasFixedSize);
        recycler.setLayoutManager(manager);
    }

    public static void clearEditText(EditText... editTexts){
        for (EditText editText:editTexts) {
            editText.setText("");
        }
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
