package com.e.gura.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.ChangePassword;
import com.e.gura.Helper;
import com.e.gura.MainActivity;
import com.e.gura.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Account extends Fragment {
    private Context ctx;
    private Helper helper;
    private Button btnChangePassword,btnLogout;
    private LinearLayout lny;
    private ProgressDialog progressDialog;
    private EditText currentPassword, newPassword, confirmPassword;
    private TextView tvName,tvPhone,tvRegdate;

    public Account() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accounting, container, false);
        ctx = view.getContext();
        helper = new Helper(ctx);
        tvName = view.findViewById(R.id.names);
        tvPhone = view.findViewById(R.id.phone);
        tvRegdate = view.findViewById(R.id.regdate);

        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx, ChangePassword.class));
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.setUserInfo("0");
                Toast.makeText(ctx,"You are logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ctx,MainActivity.class));
                getActivity().finish();
            }
        });
        checkAuthorization();
        userProfile();
        return view;
    }

    void checkAuthorization(){

        if(!helper.isLoggedIn()){
            Toast.makeText(ctx,"You should log in to continue",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ctx, MainActivity.class);
            intent.putExtra("profile","go to profile");
            ctx.
            startActivity(intent);
        }
    }
    void loadProfile(){
        String url = "https://e-gura.com/js/ajax/profile.php";
        if (helper.isNetworkConnected()) {
            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"?id="+helper.getUserId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        setLoadedProfile(obj);
                    }catch (JSONException ex){
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("update password", "Error: " + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }

    }

    void userProfile(){
        if(helper.getUserProfile() !=null) setLoadedProfile(helper.getUserProfile());
        else startActivity(new Intent(ctx,MainActivity.class));
    }
    void setLoadedProfile(JSONObject obj){
        try{
            if(obj.has("user_fname") && obj.has("user_lname") && obj.has("user_email")) {
                String names = obj.getString("user_fname") + " " + obj.getString("user_lname");
                tvName.setText(names);
                tvPhone.setText(obj.getString("user_email"));
                tvRegdate.setText(obj.getString("user_date").substring(0, 10));
            }
        }catch (JSONException ex){
        }
    }
    void changePassword(){
        //show loading box
        progressDialog.show();
        String url = "https://e-gura.com/js/ajax/main.php?andr_change_password&user_id="+helper.getUserId()+"&current_pass="+currentPassword.getText().toString()+"&new_pass="+newPassword.getText().toString()+"&conf_pass="+confirmPassword.getText().toString();
        if (helper.isNetworkConnected()) {
            if (currentPassword.getText().toString().trim().equals("") || newPassword.getText().toString().trim().equals("")) {
                Toast.makeText(ctx, "All field are required ...", Toast.LENGTH_LONG).show();
            } else if (confirmPassword.getText().toString().trim() != newPassword.getText().toString().trim()) {
                Toast.makeText(ctx, "Password does not match", Toast.LENGTH_LONG).show();
            } else {
                RequestQueue requestQueue = Volley.newRequestQueue(ctx);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if (response.trim().equals("success")) {
                            Toast.makeText(ctx,"Password updated successful",Toast.LENGTH_SHORT).show();
                        } else if(response.equals("min_length")){
                            Toast.makeText(ctx, "Minimum password characters are 8", Toast.LENGTH_LONG).show();
                        } else if(response.equals("match")){
                            Toast.makeText(ctx,"Password does not match",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ctx,"Failed to update password "+response,Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("update password", "Error: " + error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", helper.getUserId());
                        params.put("current_pass", currentPassword.getText().toString().trim());
                        params.put("new_pass", newPassword.getText().toString().trim());
                        params.put("conf_passw", confirmPassword.getText().toString().trim());
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        }

    }
}