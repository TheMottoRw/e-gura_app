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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.pages.Home;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {
    Button sngBtn;
    EditText fname, lname, email, npass, cpass;
    TextView sgnview,btnSignin;
    private Intent intent;
    private Helper helper;
    private RelativeLayout rltvLayoutLogo;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sngBtn = (Button) findViewById(R.id.signbtn);
        fname = (EditText) findViewById(R.id.firstinput);
        lname = (EditText) findViewById(R.id.lastinput);
        email = (EditText) findViewById(R.id.emailinput);
        npass = (EditText) findViewById(R.id.passwordinput);
        cpass = (EditText) findViewById(R.id.cpasswordinput);
        sgnview = (TextView) findViewById(R.id.acc_login);
        btnSignin = findViewById(R.id.btnSignin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating your account...");
        progressDialog.setCancelable(false);

        helper = new Helper(signup.this);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sngBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helper.isNetworkConnected()) SignUp();
                else Toast.makeText(signup.this,"You don't have internet connection",Toast.LENGTH_SHORT).show();
            }
        });


        intent = getIntent();
        sgnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(signup.this, MainActivity.class);
                if (intent.hasExtra("uri")) {
                    loginIntent.putExtra("uri", intent.getStringExtra("uri"));
                    loginIntent.putExtra("product", intent.getStringExtra("product"));
                }
                startActivity(loginIntent);
                finish();
            }
        });
    }
    private void SignUp(){
        progressDialog.show();
        String url = "https://e-gura.com/js/ajax/main.php?user_signup_andr=true&and_fname="+fname.getText().toString().trim()+"&and_lname="+lname.getText().toString().trim()+"&and_email="+email.getText().toString().trim()+"&and_npass="+npass.getText().toString().trim()+"&and_cpass="+cpass.getText().toString().trim();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.trim().length()>30){
                    Intent loginIntent ;
                    if(intent.hasExtra("uri")) {
                        loginIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("uri")));
                        startActivity(new Intent(signup.this, Home.class));
                    } else {
                        loginIntent = new Intent(signup.this, Home.class);
                    }
                    //save users session
                    helper.setUserInfo(response);
                    startActivity(loginIntent);
                    //Toast.makeText(signup.this, "Signup Successfully", Toast.LENGTH_LONG).show();
                }else if(response.equals("alredy")){
                    Toast.makeText(signup.this, "Sorry user already exist", Toast.LENGTH_LONG).show();
                }else if(response.equals("min_length")){
                    Toast.makeText(signup.this, "Password minimum characters are 8", Toast.LENGTH_LONG).show();
                } else if(response.equals("failed")){
                    Toast.makeText(signup.this,"Failed to signup",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(signup.this,"Something went wrong,failed to signup "+response,Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(signup.this, "This Error has found : "+error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_signup_andr", "true");
                params.put("and_fname", fname.getText().toString().trim());
                params.put("and_lname", lname.getText().toString().trim());
                params.put("and_email", email.getText().toString().trim());
                params.put("and_npass", npass.getText().toString().trim());
                params.put("and_cpass", cpass.getText().toString().trim());
                return params;
            }
        };
        if (fname.getText().toString().equals("") || lname.getText().toString().equals("") || email.getText().toString().equals("") || npass.getText().toString().equals("") || cpass.getText().toString().equals("")){
            Toast.makeText(signup.this, "Fill all forms ...", Toast.LENGTH_LONG).show();
        }else if(npass.getText().toString().trim().equals(cpass.getText().toString().trim())){
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(signup.this,"Password not match",Toast.LENGTH_LONG).show();
        }

    }
}
