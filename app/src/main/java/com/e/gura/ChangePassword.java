package com.e.gura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    public Helper helper;
    public EditText currentPassword, newPassword, confirmPassword;
    public Button btnChangePassword;
    public ProgressDialog pgdialog;
    private ImageView goBack;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        helper = new Helper(getApplicationContext());
        pgdialog = new ProgressDialog(this);
        pgdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.passwordinput);
        confirmPassword = findViewById(R.id.confirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        //Customer toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        goBack = findViewById(R.id.goBack);


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helper.isNetworkConnected())
                    changePassword();
                else Toast.makeText(getApplicationContext(),"You don't have internet",Toast.LENGTH_LONG).show();
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
             }
        });
    }

    void changePassword() {
        //show loading box
        pgdialog.show();
        String url = "https://e-gura.com/js/ajax/main.php?andr_change_password&user_id=" + helper.getUserId() + "&current_pass=" + currentPassword.getText().toString() + "&new_pass=" + newPassword.getText().toString() + "&conf_pass=" + confirmPassword.getText().toString();
        if (helper.isNetworkConnected()) {
            if (currentPassword.getText().toString().trim().equals("") || newPassword.getText().toString().trim().equals("")) {
                Toast.makeText(ChangePassword.this, "All field are required ...", Toast.LENGTH_LONG).show();
            } else if (!confirmPassword.getText().toString().trim().equals(newPassword.getText().toString().trim())) {
                Toast.makeText(ChangePassword.this, "Password does not match", Toast.LENGTH_LONG).show();
            } else {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pgdialog.dismiss();
                        if (response.trim().equals("success")) {
                            Toast.makeText(ChangePassword.this, "Password updated successful", Toast.LENGTH_SHORT).show();
                        } else if (response.equals("min_length")) {
                            Toast.makeText(ChangePassword.this, "Minimum password characters are 8", Toast.LENGTH_LONG).show();
                        } else if (response.equals("match")) {
                            Toast.makeText(ChangePassword.this, "Password does not match", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePassword.this, "Failed to update password " + response, Toast.LENGTH_SHORT).show();
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