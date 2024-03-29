package com.e.gura.pages;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.Helper;
import com.e.gura.adapters.CategoryAdapter;
import com.e.gura.adapters.HorizontalCategoryAdapter;
import com.e.gura.R;
import com.e.gura.adapters.CategoryAdapter;
import com.e.gura.utils.DummyData;
import com.google.android.material.snackbar.Snackbar;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.http.HEAD;


public class Category extends Fragment {
    private TextView tvNoInternet;
    private Helper helper;
    private RequestQueue mQueue;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CategoryAdapter adapter;
    public Context ctx;
    private JSONArray productsArray, searchedCategoriesArray, allCategoryArray;
    public int len, lastCount = 0, defaultCount = 75, loopStop = 0, page = 0, delay = 1000;
    public RotateLoading rotateLoading;

    public Category() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ctx = view.getContext();
        helper = new Helper(ctx);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(layoutManager);
        tvNoInternet = view.findViewById(R.id.tvNoInternet);
        rotateLoading = view.findViewById(R.id.loading);

        //network connectivity
        helper.toggleNetworkConnectivityTextView(tvNoInternet);

        if (helper.isNetworkConnected()){;
            rotateLoading.start();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    loadCategories();
                }
            });
            t.start();
        } else{
            CategoryAdapter adapter = new CategoryAdapter(ctx, DummyData.getCategoryDummies());
            recyclerView.setAdapter(adapter);
        }


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EditText edtKeyword = activity.findViewById(R.id.edtKeyword);
        edtKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 2) {
                    searchInArray(s + "");
                } else if (s.toString().length() == 0) setLoadedCategories("all");
            }
        });
    }

    private void loadCategories() {
        String url = "https://mobile.e-gura.com/main/view.php?andr_categories_list";
        mQueue = Volley.newRequestQueue(ctx);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (rotateLoading.isStart()) rotateLoading.stop();

                        try {
                            //set products' category to recyclerview
                            allCategoryArray = new JSONArray(response);
                            adapter = new CategoryAdapter(ctx, allCategoryArray);
                            recyclerView.setAdapter(adapter);
                            // progressBar.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (rotateLoading.isStart()) rotateLoading.stop();
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    private void searchInArray(String keyword) {
        searchedCategoriesArray = new JSONArray();
        if (allCategoryArray.length() == 0)
            Snackbar.make(recyclerView, "No products found", Snackbar.LENGTH_LONG).show();
        else {
            for (int i = 0; i < allCategoryArray.length(); i++) {
                try {
                    JSONObject obj = allCategoryArray.getJSONObject(i);
                    if (obj.getString("cat_name").toLowerCase().contains(keyword.toLowerCase()))
                        searchedCategoriesArray.put(obj);
                } catch (JSONException ex) {

                }
            }
            //set loaded products to recylerview
            setLoadedCategories("search");
        }
    }

    private void setLoadedCategories(String loadType) {
        JSONArray dataArray = new JSONArray();

        lastCount = 0;
        if (loadType.equals("all")) dataArray = allCategoryArray;
        else if (loadType.equals("search")) dataArray = searchedCategoriesArray;
        loopStop = dataArray.length() > defaultCount ? defaultCount : dataArray.length();

        CategoryAdapter categoryAdapter = new CategoryAdapter(ctx, dataArray);
        recyclerView.setAdapter(categoryAdapter);
    }

}