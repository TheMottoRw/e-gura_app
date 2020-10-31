package com.e.gura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.pages.Home;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText eml, pss, edtEmail, edtResetPhone;
    Button lgn, sgnup, forgot, buttonBackToLogin, btnResetButton;
    TextView resp, tvNoInternet;
    private Helper helper;
    private Intent intent;
    private RelativeLayout loginLayout, resetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new Helper(MainActivity.this);
        eml = (EditText) findViewById(R.id.edtPhone);
        pss = (EditText) findViewById(R.id.passwordinput);
        tvNoInternet = findViewById(R.id.tvNoInternet);
        lgn = (Button) findViewById(R.id.signbtn);
        sgnup = (Button) findViewById(R.id.newaccountss);
        resp = (TextView) findViewById(R.id.resp_view);
        forgot = findViewById(R.id.forgot);
        //reset password
        edtResetPhone = findViewById(R.id.resetPhone);
        btnResetButton = findViewById(R.id.resetButton);

        loginLayout = findViewById(R.id.loginLayout);
        resetLayout = findViewById(R.id.resetLayout);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);

        //check if comes from stage of buying
        intent = getIntent();

        lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helper.isNetworkConnected()) Login();
                else
                    Toast.makeText(MainActivity.this, "You don't have internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        sgnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(MainActivity.this, signup.class);
                if (intent.hasExtra("uri")) {
                    loginIntent.putExtra("uri", intent.getStringExtra("uri"));
                    loginIntent.putExtra("product", intent.getStringExtra("product"));
                }
                startActivity(loginIntent);
                finish();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginLayout.setVisibility(View.GONE);
                resetLayout.setVisibility(View.VISIBLE);
            }
        });
        buttonBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginLayout.setVisibility(View.VISIBLE);
                resetLayout.setVisibility(View.GONE);
            }
        });
        //network connectivity
        helper.toggleNetworkConnectivityTextView(tvNoInternet);

        //reset method
        btnResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.toggleNetworkConnectivityTextView(tvNoInternet);
    }

    private void Login() {
        String url = "https://e-gura.com/js/ajax/main.php";
        if (helper.isNetworkConnected()) {
            if (eml.getText().toString().equals("") || pss.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "All Forms are required ...", Toast.LENGTH_LONG).show();
            } else {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Login response: ", response);
                        if (!response.trim().equals("failed")) {
                            helper.setUserInfo(response);
                            Intent loginIntent;
                            if (intent.hasExtra("uri")) {
                                loginIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("uri")));
                            } else if (intent.hasExtra("upload")) {
                                loginIntent = new Intent(MainActivity.this, UploadProduct.class);
                            } else if(intent.hasExtra("profile")){
                                loginIntent = new Intent(MainActivity.this,Profile.class);
                                loginIntent.putExtra("profile","");
                            } else if(intent.hasExtra("cart")){
                                loginIntent = new Intent(MainActivity.this,ProductInfo.class);
                                loginIntent.putExtra("product",getIntent().getStringExtra("product"));
                            } else {
                                loginIntent = new Intent(MainActivity.this, Navigator.class);
                            }
                            finish();
                            startActivity(loginIntent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "This Error has found : " + error.toString(), Toast.LENGTH_LONG).show();
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

    private void reset() {
        String url = "https://e-gura.com/js/ajax/main.php?andr_reset_pass&user_email=" + edtEmail.getText().toString().trim();
        if (helper.isNetworkConnected()) {
            if (eml.getText().toString().equals("") || pss.getText().toString().equals("")) {
                Toast.makeText(MainActivity.this, "All Forms are required ...", Toast.LENGTH_LONG).show();
            } else {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.trim().equals("failed")) {
                            helper.setUserInfo(response);
                            Toast.makeText(MainActivity.this, "Password changed,login again...", Toast.LENGTH_LONG).show();
                            Intent loginIntent = new Intent(MainActivity.this, Home.class);
                            startActivity(loginIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "Failed To Login", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "This Error has found : " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("phone", edtResetPhone.getText().toString().trim());
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        }
    }

}
