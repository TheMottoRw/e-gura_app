package com.e.gura;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class ProductInfo extends AppCompatActivity {
    private Helper helper;
    private TextView prodName, prodPu, prodSize, prodColor, prodChipping,loading,tvNoInternet;
    private EditText prodQty;
    private ImageView[] img;
    private Button btnBuyProduct;
    private int targetImgView = 0,prodImgLen;
    private Intent data;
    public ProgressDialog progressDialog;
    private ImageView imgUpload,img1,img2,img3;
    private GifImageView imgLoading;
    private RelativeLayout rltLayoutLogo,relativeLayoutGoToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        helper = new Helper(ProductInfo.this);

        tvNoInternet = findViewById(R.id.tvNoInternet);
        imgUpload = findViewById(R.id.imgUpload);
        imgLoading = findViewById(R.id.imgLoading);
        img1 = findViewById(R.id.productMainLogo);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        rltLayoutLogo = findViewById(R.id.rltLayoutLogo);
        relativeLayoutGoToProfile = findViewById(R.id.relativeLayoutGoToProfile);

        prodName = findViewById(R.id.prodName);
        prodPu = findViewById(R.id.prodPu);
        prodSize = findViewById(R.id.prodSize);
        prodQty = findViewById(R.id.prodQty);
        prodColor = findViewById(R.id.prodColor);
        prodChipping = findViewById(R.id.prodChipping);
        loading = findViewById(R.id.loading);
        btnBuyProduct = findViewById(R.id.btnBuyProduct);

        progressDialog  =new ProgressDialog(ProductInfo.this);
        progressDialog.setMessage("Loading images...");
        /*progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);*/
        progressDialog.setCancelable(true);

        data = getIntent();
        btnBuyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prodQty.getText().toString().trim().equals("") || prodQty.getText().toString().trim().equals("0"))
                    Snackbar.make(prodQty,"Quantity should be a number greater than 0",Snackbar.LENGTH_LONG).show();
                else
                paymentRequest();
            }
        });
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductInfo.this,UploadProduct.class));
            }
        });
        rltLayoutLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        relativeLayoutGoToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductInfo.this,Profile.class));
            }
        });

        try {
            JSONObject obj = new JSONObject(data.getStringExtra("product"));
            prodName.setText(obj.getString("product_name"));
            prodPu.setText(obj.getString("product_price")+" RWF");
            prodChipping.setText("Shipping fee: " + obj.getString("product_chipping"));
            prodColor.setText("Color: " + obj.getString("product_color"));
            prodSize.setText("Size:" + obj.getString("product_size"));
            img = new ImageView[]{};
            //show loading products
           // loading.setVisibility(View.VISIBLE);
            prodImgLen = obj.has("product_file_3")?3:(obj.has("product_file_2")?2:1);
            progressDialog.setMax(prodImgLen);
           // progressDialog.show();
            imgLoading.setVisibility(View.GONE);
            Glide.with(ProductInfo.this)
                    .load(obj.getString("product_file"))
                    .placeholder(R.drawable.logo_egura) //placeholder
                    .centerCrop()
                    .error(R.drawable.logo_egura) //error
                    .into(img1);
            Glide.with(ProductInfo.this)
                    .load(obj.getString("product_file_2"))
                    .placeholder(R.drawable.logo_egura) //placeholder
                    .centerCrop()
                    .error(R.drawable.logo_egura) //error
                    .into(img2);
            Glide.with(ProductInfo.this)
                    .load(obj.getString("product_file_3"))
                    .placeholder(R.drawable.logo_egura) //placeholder
                    .centerCrop()
                    .error(R.drawable.logo_egura) //error
                    .into(img3);

                /*for (int i = 0; i < 3; i++) {
                    String fileUrl = "product_file";
                    fileUrl += (i != 0 ? "_" + (i + 1) : "");
                    targetImgView = i;
                    *//*f(obj.has(fileUrl) && obj.getString(fileUrl) != null)
                        new Background().execute(new String[]{obj.getString(fileUrl), String.valueOf(i)});*//*
                }*/
        } catch (
                JSONException ex) {

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        helper.toggleNetworkConnectivityTextView(tvNoInternet);
    }

    void paymentRequest(){
        String url = "https://e-gura.com/main/view.php?andr_proceed_with_pay_pr_details&user_id"+helper.getUserId()+"&product_id=";
        try {
            //opening browser
            JSONObject obj = new JSONObject(data.getStringExtra("product"));
            url += obj.getString("product_id");
            url+="&quantity="+prodQty.getText().toString();
            //send product request to pay
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Intent intent;
                    //Log.d("Pay request response: ",response);
                    if (!helper.getUserId().equals("0")) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(response));
                        startActivity(intent);

                    } else {
                        //check if hasn't account redirect to login
                        intent = new Intent(ProductInfo.this, MainActivity.class);
                    }
                    intent.putExtra("product", data);
                    intent.putExtra("uri", response);
                    startActivity(intent);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("request error",error.getMessage());
                    //Toast.makeText(getApplicationContext(), "This Error has found : " + error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }
            };
            requestQueue.add(stringRequest);

        } catch (JSONException ex) {

        }
    }
}