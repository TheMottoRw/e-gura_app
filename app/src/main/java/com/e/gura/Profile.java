package com.e.gura;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private Helper helper;
    private Button btnChangePassword,btnLogout;
    private LinearLayout lny;
    private ProgressDialog progressDialog;
    private EditText currentPassword, newPassword, confirmPassword;
    private TextView tvName,tvPhone,tvRegdate;
    private RelativeLayout rltvLayoutDrawer,rltLayoutLogo;
    private ImageView imgUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        helper = new Helper(Profile.this);
        tvName = findViewById(R.id.names);
        tvPhone = findViewById(R.id.phone);
        tvRegdate = findViewById(R.id.regdate);
        rltvLayoutDrawer = findViewById(R.id.rltvLayoutDrawer);
        rltLayoutLogo = findViewById(R.id.rltLayoutLogo);
        imgUpload = findViewById(R.id.imgUpload);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating password");
        currentPassword = new EditText(this);
        currentPassword.setHint("Enter old password");
        newPassword = new EditText(this);
        newPassword.setHint("Enter new password");
        confirmPassword = new EditText(this);
        confirmPassword.setHint("Confirm new password");
        lny = new LinearLayout(this);

        currentPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        lny.setOrientation(LinearLayout.VERTICAL);
        final AlertDialog alert = new AlertDialog.Builder(Profile.this).create();
        alert.setTitle("Change password");
        alert.setMessage("Fill form below to change password");


        lny.addView(currentPassword);
        lny.addView(newPassword);
        lny.addView(confirmPassword);

        alert.setView(lny);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.setUserInfo("0");
                Toast.makeText(Profile.this,"You are logged out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,UploadProduct.class));
            }
        });
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
                        else Toast.makeText(Profile.this,"Password not match",Toast.LENGTH_LONG).show();
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

        rltvLayoutDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rltLayoutLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //check authorizaiton
        checkAuthorization();
        userProfile();
    }
    void checkAuthorization(){

        if(!helper.isLoggedIn()){
            Toast.makeText(Profile.this,"You should log in to continue",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Profile.this,MainActivity.class);
            intent.putExtra("profile","go to profile");
            finish();
            startActivity(intent);
        }
    }
    void loadProfile(){
        String url = "https://e-gura.com/js/ajax/profile.php";
        if (helper.isNetworkConnected()) {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
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
        else startActivity(new Intent(Profile.this,MainActivity.class));
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
                Toast.makeText(Profile.this, "All field are required ...", Toast.LENGTH_LONG).show();
            } else if (confirmPassword.getText().toString().trim() != newPassword.getText().toString().trim()) {
                Toast.makeText(Profile.this, "Password does not match", Toast.LENGTH_LONG).show();
            } else {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("change password debug ",response);
                        progressDialog.dismiss();
                        if (response.trim().equals("success")) {
                            Toast.makeText(Profile.this,"Password updated successful",Toast.LENGTH_SHORT).show();
                        } else if(response.equals("min_length")){
                            Toast.makeText(Profile.this, "Minimum password characters are 8", Toast.LENGTH_LONG).show();
                        } else if(response.equals("match")){
                            Toast.makeText(Profile.this,"Password does not match",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Profile.this,"Failed to update password "+response,Toast.LENGTH_SHORT).show();
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
