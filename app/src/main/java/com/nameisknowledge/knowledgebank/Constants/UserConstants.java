package com.nameisknowledge.knowledgebank.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;

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
    public static final String CURRENT_AREA_ATTACK_POINTS = "CURRENT_AREA_ATTACK_POINTS";
    public static final String CURRENT_NOTIFICATION_TOKEN = "CURRENT_NOTIFICATION_TOKEN";
    public static final int DEFAULT_AREA_ATTACK_POINTS = 10;
    public static final int REWARD_AREA_ATTACK_POINTS = 3;
    public static final int DOUBLE_REWARD_AREA_ATTACK_POINTS = 5;

    public static UserMD getCurrentUser(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String username = preferences.getString(CURRENT_USERNAME , "");
        String uid = preferences.getString(CURRENT_UID , "");
        String email = preferences.getString(CURRENT_EMAIL , "");
        String password = preferences.getString(CURRENT_PASSWORD , "");
        String gender = preferences.getString(CURRENT_GENDER , "");
        String avatar = preferences.getString(CURRENT_AVATAR , "");
        String notificationToken = preferences.getString(CURRENT_NOTIFICATION_TOKEN , "");
        int areaAttackPoints = preferences.getInt(CURRENT_AREA_ATTACK_POINTS  , 10);
        long birth = preferences.getLong(CURRENT_BIRTHDATE , 0);
        long creation = preferences.getLong(CURRENT_CREATION_DATE , 0);

        Date  birthdate = new Date(birth);
        Date  creationDate = new Date(creation);

       return new UserMD(uid , username , email , password , gender , avatar  , notificationToken, birthdate , creationDate , areaAttackPoints) ;
    }

    public static void setCurrentUser(UserMD user , Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putString(UserConstants.CURRENT_UID , user.getUid());
        editor.putString(UserConstants.CURRENT_AVATAR , user.getAvatarRes());
        editor.putString(UserConstants.CURRENT_GENDER , user.getGender());
        editor.putString(UserConstants.CURRENT_PASSWORD , user.getPassword());
        editor.putString(UserConstants.CURRENT_EMAIL , user.getEmail());
        editor.putString(UserConstants.CURRENT_USERNAME , user.getUsername());
        editor.putString(UserConstants.CURRENT_NOTIFICATION_TOKEN , user.getNotificationToken());
        editor.putInt(UserConstants.CURRENT_AREA_ATTACK_POINTS , user.getAreaAttackPoints());
        editor.putLong(UserConstants.CURRENT_BIRTHDATE , user.getBirthdate().getTime());
        editor.putLong(UserConstants.CURRENT_CREATION_DATE , user.getCreationDate().getTime());

        editor.apply();
    }
}
