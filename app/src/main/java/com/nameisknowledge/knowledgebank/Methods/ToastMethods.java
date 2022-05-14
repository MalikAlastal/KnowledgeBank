package com.nameisknowledge.knowledgebank.Methods;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Toast;

import com.nameisknowledge.knowledgebank.R;

import es.dmoral.toasty.Toasty;


public class ToastMethods {

    private Toast toast ;
    private Context context ;

    public ToastMethods(Context context){
        toast = new Toast(context);
        this.context = context ;
    }

    public void success(String s){
       toast.cancel();
       toast = Toasty.success(context , s);
       toast.show();
    }

    public void error(String s){
       toast.cancel();
       toast = Toasty.error(context , s);
       toast.show();
    }

    public void info(String s){
       toast.cancel();
       toast = Toasty.info(context , s);
       toast.show();
    }

    public void normal(String s){
       toast.cancel();
       toast = Toasty.normal(context , s);
       toast.show();
    }

    public void warning(String s){
       toast.cancel();
       toast = Toasty.warning(context , s);
       toast.show();
    }


}
