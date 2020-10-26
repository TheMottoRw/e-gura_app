package com.e.gura.pages;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.Helper;
import com.e.gura.MainActivity;
import com.e.gura.Profile;
import com.e.gura.R;
import com.e.gura.UploadProduct;
import com.e.gura.adapters.HorizontalCategoryAdapter;
import com.e.gura.adapters.ProductAdapter;
import com.e.gura.adapters.SliderAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class Home extends Fragment {
    private TextView tvNoInternet, loading;
    private RequestQueue mQueue;
    private ProgressDialog progressBar;
    private GridLayout gridLayout;
    public ImageView img, imgUpload;
    public ArrayList arr;
    private Button loadMore;
    private Helper helper;
    private RecyclerView recyclerView;
    private RecyclerView gridRecyclerView;
    private HorizontalCategoryAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.LayoutManager layoutManager;
    private JSONArray productsArray, searchedProductsArray, allProductArray;
    private GifImageView imgLoading;
    private Context ctx;
    private Activity activity;
    public Runnable runnable;
    public Handler handler;
    public int len, lastCount = 0, defaultCount = 75, loopStop = 0, page = 0, delay = 5000;
    private String[] sliderImageArray;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ctx = view.getContext();
//        setContentView(R.layout.activity_home);

        gridLayout = view.findViewById(R.id.grdView);
        arr = new ArrayList();
        helper = new Helper(ctx);
        imgLoading = view.findViewById(R.id.imgLoading);
        //recyclerview
        gridRecyclerView = view.findViewById(R.id.gridRecyclerView);
        layoutManager = new GridLayoutManager(ctx, 2);
        gridRecyclerView.setLayoutManager(layoutManager);
        //end recyclerview

        tvNoInternet = view.findViewById(R.id.tvNoInternet);
        loadMore = view.findViewById(R.id.loadMore);
        // loading = findViewById(R.id.loading);

        //show & hide textview no internet
        helper.toggleNetworkConnectivityTextView(tvNoInternet);

        recyclerView = view.findViewById(R.id.recyclerView);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadedProducts("all");
            }
        });

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mQueue = Volley.newRequestQueue(ctx);
        progressBar = new ProgressDialog(ctx);
        progressBar.setMessage("Loading products...");
        progressBar.setCancelable(true);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadCategories();
                setProductsUrl("all","0");
            }
        });
        t.start();


        //Image slider
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        sliderImageArray = new String[7];

        return view;
    }

    public int progressCounter() {
        return 100 * (lastCount / loopStop);
    }

    public void loadProfile() {
        startActivity(new Intent(ctx, Profile.class));
    }

    public void setProductsUrl(String by, String id) {
        String url = "";
        progressBar.setMessage("Loading...");
        switch (by) {
            case "all":
                url = "https://e-gura.com/main/view.php?and_products_imgs";
                break;
            case "category":
                url = "https://e-gura.com/js/ajax/main.php?all_products_categories";
                break;
            case "product_based_category":
                url = "https://e-gura.com/js/ajax/main.php?categories_products=" + id;
                loadProductsToSearchIn();//loading products used to search in
                break;
            case "search":
                progressBar.setMessage("Searching...");
                imgLoading.setVisibility(View.VISIBLE);
                url = "https://e-gura.com/js/ajax/main.php?andr_prod_search&content=keyword";//add keyword
                break;
            default:
                url = "https://e-gura.com/main/view.php?and_products_imgs";
        }
        //progressBar.show();
        loadProducts(url);
    }

    public void loadProducts(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        //Log.e("escaper", res.replace("\\", ""));
                        try {
                            //JSONObject response = new JSONObject(res.replace("\\", ""));
                            gridLayout.removeAllViews();
                            productsArray = response.getJSONArray("and_products_imgs");
                            if (productsArray.length() == 0) {
                                Toast.makeText(ctx, "No products found", Toast.LENGTH_LONG).show();
                                imgLoading.setVisibility(View.GONE);
                            }
//                            if (!activity.getIntent().hasExtra("category"))
                                allProductArray = productsArray;
                            len = productsArray.length();
                            loopStop = len > defaultCount ? defaultCount : len;
                            setLoadedProducts("all");

                            //end of custom setloaded product */
                            progressBar.dismiss();
                        } catch (JSONException e) {
                            Log.d("JSON Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error volley", error.getMessage());
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    public void loadProductsToSearchIn() {
        Log.d("prod to search in", "Query runned");
        String localUrl = "https://e-gura.com/main/view.php?and_products_imgs";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, localUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("prod to search in resp", response.toString());
                        //Log.e("escaper", res.replace("\\", ""));
                        try {
                            if (response.has("and_products_imgs"))
                                allProductArray = response.getJSONArray("and_products_imgs");
                        } catch (JSONException e) {
                            Log.d("JSON Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error volley", error.getMessage());
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    private void searchInArray() {
        searchedProductsArray = new JSONArray();
        if (allProductArray.length() == 0)
            Snackbar.make(loadMore, "No products found", Snackbar.LENGTH_LONG).show();
        else {
            Log.d("all prod array", allProductArray.toString());
            for (int i = 0; i < allProductArray.length(); i++) {
                try {
                    JSONObject obj = allProductArray.getJSONObject(i);
                    if (obj.getString("product_name").toLowerCase().contains(loadMore.getText().toString().toLowerCase()) || obj.getString("product_descr").toLowerCase().contains(loadMore.getText().toString().toLowerCase()))
                        searchedProductsArray.put(obj);
                } catch (JSONException ex) {
                    Log.d("json err", ex.getMessage());
                }
            }
            //set loaded products to recylerview
            setLoadedProducts("search");
        }
    }

    private void setLoadedProducts(String loadType) {
        JSONArray dataArray = new JSONArray();

        lastCount = 0;
        if (loadType.equals("all")) dataArray = productsArray;
        else if (loadType.equals("search")) dataArray = searchedProductsArray;
        loopStop = dataArray.length() > defaultCount ? defaultCount : dataArray.length();
        Log.d("display data ", loadType + " - " + dataArray.toString());

        ProductAdapter productAdapter = new ProductAdapter(ctx, dataArray);
        gridRecyclerView.setAdapter(productAdapter);

        populateSlider(productsArray);
    }

    private void populateSlider(JSONArray jsonArray) {
        try {
            int loopLen = jsonArray.length() > 7 ? 7 : jsonArray.length();
            Toast.makeText(ctx,"Limit len "+loopLen,Toast.LENGTH_SHORT).show();
            for (int i = 0; i < loopLen; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
//                if(obj.has("product_profile") && !obj.isNull("product_file"))
                    sliderImageArray[i] = obj.getString("product_file");
//                Toast.makeText(ctx,obj.getString("product_profile"),Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(ctx,"Slide len "+sliderImageArray.toString(),Toast.LENGTH_SHORT).show();
            loadSlider(sliderImageArray);
        } catch (JSONException ex) {
            Log.e("SlideErr",ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadSlider(String[] arr) {
        Toast.makeText(ctx,arr.toString(),Toast.LENGTH_SHORT).show();
        final SliderAdapter adapterView = new SliderAdapter(ctx, arr);
        mViewPager.setAdapter(adapterView);
        //autoscoll view pager

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                if (adapterView.getCount() == page) {
                    page = 0;
                } else {
                    page++;
                }
                mViewPager.setCurrentItem(page, true);
                handler.postDelayed(this, delay);
            }
        };

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void loadCategories() {
        String url = "https://e-gura.com/js/ajax/main.php?all_products_categories";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("response", response.toString());
                        //Log.e("escaper", res.replace("\\", ""));
                        try {
                            //load all products
                            // Toast.makeText(home.this,String.valueOf(getIntent().hasExtra("category")),Toast.LENGTH_SHORT).show();
//                            if (getArguments().hasExtra("category"))
//                                setProductsUrl("product_based_category", getIntent().getStringExtra("category"));
//                            else setProductsUrl("all", "0");

                            //set products' category to recyclerview
                            adapter = new HorizontalCategoryAdapter(ctx, response.getJSONArray("all_categories"));
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