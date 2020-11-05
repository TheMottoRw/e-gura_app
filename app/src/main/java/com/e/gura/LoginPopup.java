package com.e.gura;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginPopup extends AppCompatActivity {
    public ProgressDialog pgdialog;
    public Helper helper;
    public EditText eml,pss;
    public Button signbtn;
    public TextView tvSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_popup);
        helper = new Helper(getApplicationContext());
        helper.setPopped(true);

        eml = findViewById(R.id.edtPhone);
        pss = findViewById(R.id.passwordinput);
        signbtn = findViewById(R.id.signbtn);
        tvSignup = findViewById(R.id.tvSignup);
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),signup.class));
            }
        });
        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

    }
    private void Login() {
        pgdialog.show();
        String url = "https://e-gura.com/js/ajax/main.php";
        if (helper.isNetworkConnected()) {
            if (eml.getText().toString().equals("") || pss.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "All Forms are required ...", Toast.LENGTH_LONG).show();
            } else {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pgdialog.dismiss();
                        if (!response.trim().equals("failed")) {
                            helper.setUserInfo(response);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "This Error has found : " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", eml.getText().toString().trim());
                        params.put("password", pss.getText().toString().trim());
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        20000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
            }
        }
    }

}