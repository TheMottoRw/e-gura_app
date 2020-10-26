package com.e.gura.pages;

import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.Helper;
import com.e.gura.adapters.CategoryAdapter;
import com.e.gura.adapters.HorizontalCategoryAdapter;
import com.e.gura.R;

import org.json.JSONException;
import org.json.JSONObject;


public class Category extends Fragment {

    private Helper helper;
    private RequestQueue mQueue;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CategoryAdapter adapter;
    public Context ctx;

    public Category() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ctx = view.getContext();
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(ctx);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadCategories();
            }
        });
        t.start();

        return view;
    }

    private void loadCategories() {
        String url = "https://e-gura.com/js/ajax/main.php?all_products_categories";
        mQueue = Volley.newRequestQueue(ctx);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("response", response.toString());
                        //Log.e("escaper", res.replace("\\", ""));
                        try {
                            //set products' category to recyclerview
                            adapter = new CategoryAdapter(ctx, response.getJSONArray("all_categories"));
                            recyclerView.setAdapter(adapter);
                            // progressBar.dismiss();
                        } catch (JSONException e) {
                            Log.d("JSON Error", e.getMessage());
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