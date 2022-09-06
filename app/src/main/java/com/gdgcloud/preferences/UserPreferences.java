package com.gdgcloud.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class UserPreferences {

    Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String IS_LOGIN = "isLogin";
    public static final String IS_EMAIL_ID ="mailId";
    public static final String IS_PASSWORD ="isPassword";

    public UserPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("SigninFile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void SignIn(String email, String password){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(IS_EMAIL_ID,email);
        editor.putString(IS_PASSWORD,password);
        editor.commit();
    }

    public HashMap<String,String> userData(){
        HashMap<String, String> user = new HashMap<>();
        user.put(IS_EMAIL_ID,sharedPreferences.getString(IS_EMAIL_ID,null));
        user.put(IS_PASSWORD,sharedPreferences.getString(IS_PASSWORD,null));
        return user;
    }

//    public boolean checkUser(){
//        if (sharedPreferences.getBoolean(IS_LOGIN,false)){
//            return true;
//        }else {
//            return false;
//        }
//    }


    public void logOut(){
        editor.clear();
        editor.commit();
    }

}
