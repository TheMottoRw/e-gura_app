package com.e.gura.pages;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.Helper;
import com.e.gura.R;
import com.e.gura.adapters.CategoryAdapter;
import com.e.gura.adapters.SubCategoryAdapter;
import com.e.gura.utils.DummyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Subcategory extends AppCompatActivity {

    public TextView tvNoInternet;
    private RequestQueue mQueue;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SubCategoryAdapter adapter;
    public Context ctx;
    private Helper helper;
    //redesign feature
    private ImageView goBack;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);
        helper = new Helper(getApplicationContext());

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        goBack = findViewById(R.id.goBack);
        tvNoInternet = findViewById(R.id.tvNoInternet);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //network connectivity
        helper.toggleNetworkConnectivityTextView(tvNoInternet);
        if (helper.isNetworkConnected()) {
            loadSubcategories();
        }
        else{
            SubCategoryAdapter adapter = new SubCategoryAdapter(getApplicationContext(), DummyData.getCategoryDummies());
            recyclerView.setAdapter(adapter);
        }
    }

    public void loadSubcategories() {
        Intent intent = getIntent();

        if (intent.hasExtra("category")) {
            toolbar.setTitle(intent.getStringExtra("category"));
            String url = "https://mobile.e-gura.com/main/view.php?andr_sub_categories_on_cat&category=" + intent.getStringExtra("category");
            mQueue = Volley.newRequestQueue(getApplicationContext());

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.d("response", response.toString());
                            //Log.e("escaper", res.replace("\\", ""));
                            try {
                                JSONArray arr = new JSONArray(response);
                                //set products' category to recyclerview
                                adapter = new SubCategoryAdapter(getApplicationContext(), arr);
                                recyclerView.setAdapter(adapter);
                                // progressBar.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mQueue.add(request);
        }
    }
}