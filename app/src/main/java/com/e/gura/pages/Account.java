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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.e.gura.Helper;
import com.e.gura.MainActivity;
import com.e.gura.Profile;
import com.e.gura.R;
import com.e.gura.UploadProduct;

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
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Updating password");
        currentPassword = new EditText(ctx);
        currentPassword.setHint("Enter old password");
        newPassword = new EditText(ctx);
        newPassword.setHint("Enter new password");
        confirmPassword = new EditText(ctx);
        confirmPassword.setHint("Confirm new password");
        lny = new LinearLayout(ctx);

        currentPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        lny.setOrientation(LinearLayout.VERTICAL);
        final AlertDialog alert = new AlertDialog.Builder(ctx).create();
        alert.setTitle("Change password");
        alert.setMessage("Fill form below to change password");


        lny.addView(currentPassword);
        lny.addView(newPassword);
        lny.addView(confirmPassword);

        alert.setView(lny);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alert.setButton(DialogInterface.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                            changePassword();
                            lny.removeAllViews();
                            alert.setView(null);
                            alert.dismiss();
                        }
                        else Toast.makeText(ctx,"Password not match",Toast.LENGTH_LONG).show();
                    }
                });
                alert.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
                alert.show();
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
                        Log.d("json debug",ex.getMessage());
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
            tvName.setText(obj.getString("user_fname")+" "+obj.getString("user_lname"));
            tvPhone.setText(obj.getString("user_email"));
            tvRegdate.setText(obj.getString("user_date").substring(0,10));
        }catch (JSONException ex){
            Log.d("Json debug",ex.getMessage());
        }
    }
    void changePassword(){
        //show loading box
        progressDialog.show();
        String url = "https://e-gura.com/js/ajax/main.php?andr_change_password&user_id="+helper.getUserId()+"&current_pass="+currentPassword.getText().toString()+"&new_pass="+newPassword.getText().toString()+"&conf_pass="+confirmPassword.getText().toString();
        Log.d("url change password",url);
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
                        Log.d("change password debug ",response);
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