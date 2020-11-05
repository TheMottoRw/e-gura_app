package com.e.gura;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Helper {
    public Context ctx;

    public Helper(Context context) {
        ctx = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public void toggleNetworkConnectivityTextView(TextView tv){
        if(isNetworkConnected()) tv.setVisibility(View.GONE);
        else tv.setVisibility(View.VISIBLE);
        tv.refreshDrawableState();
    }

    public void setUserInfo(String userid) {
        SharedPreferences.Editor sharedPreferences = ctx.getSharedPreferences("uinfo", Context.MODE_PRIVATE).edit();
        sharedPreferences.putString("userid", userid);
        sharedPreferences.putBoolean("has_account", true);
        sharedPreferences.commit();
    }

    public String getUserId() {
        String userData = ctx.getSharedPreferences("uinfo", Context.MODE_PRIVATE).getString("userid", "0"),
                userId = "";

        try {
            if(userData.equals("0")) return userData;

            JSONObject obj = new JSONObject(userData);
            userId = getUserProfile().getString("user_id");
        } catch (JSONException ex) {

        }
        return userId;
    }

    public JSONObject getUserProfile() {
        JSONObject obj = null;
        String userData = ctx.getSharedPreferences("uinfo", Context.MODE_PRIVATE).getString("userid", "0");
        try {
            if(userData.equals("0")) return obj;
            obj = new JSONObject(userData).getJSONArray("andr_user_login").getJSONObject(0);
        } catch (JSONException ex) {

        }
        return obj;
    }

    public boolean hasAccount() {
        return ctx.getSharedPreferences("uinfo", Context.MODE_PRIVATE).getBoolean("has_account", false);
    }
    public boolean hasPopped(){
        return ctx.getSharedPreferences("uinfo", Context.MODE_PRIVATE).getBoolean("has_popped", false);
    }
    public void setPopped(boolean status){
        SharedPreferences.Editor sharedPreferences = ctx.getSharedPreferences("uinfo", Context.MODE_PRIVATE).edit();
        sharedPreferences.putBoolean("has_popped",status);
        sharedPreferences.apply();
    }
    public boolean isLoggedIn(){
        return getUserProfile() != null;
    }

}
