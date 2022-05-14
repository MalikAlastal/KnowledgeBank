package com.nameisknowledge.knowledgebank.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;

import java.util.Calendar;
import java.util.Date;

public class UserConstants {
    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    // أصغر عمر للمستخدم
    public static final int MIN_AGE = 8 ;

    public static final String CURRENT_USERNAME = "CURRENT_USERNAME";
    public static final String CURRENT_UID = "CURRENT_UID";
    public static final String CURRENT_EMAIL = "CURRENT_EMAIL";
    public static final String CURRENT_PASSWORD = "CURRENT_PASSWORD";
    public static final String CURRENT_GENDER = "CURRENT_GENDER";
    public static final String CURRENT_AVATAR = "CURRENT_AVATAR";
    public static final String CURRENT_BIRTHDATE = "CURRENT_BIRTHDATE";
    public static final String CURRENT_CREATION_DATE = "CURRENT_CREATION_DATE";

    public static UserMD getCurrentUser(Context context){


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String username = preferences.getString(CURRENT_USERNAME , "");
        String uid = preferences.getString(CURRENT_UID , "");
        String email = preferences.getString(CURRENT_EMAIL , "");
        String password = preferences.getString(CURRENT_PASSWORD , "");
        String gender = preferences.getString(CURRENT_GENDER , "");
        String avatar = preferences.getString(CURRENT_AVATAR , "");

        long birth = preferences.getLong(CURRENT_BIRTHDATE , 0);
        long creation = preferences.getLong(CURRENT_CREATION_DATE , 0);

        Date  birthdate = new Date(birth);
        Date  creationDate = new Date(creation);

       return new UserMD(uid , username , email , password , gender , avatar , birthdate , creationDate) ;
    }
}
