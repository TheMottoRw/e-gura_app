package com.e.gura.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.Browser;
import com.e.gura.Helper;
import com.e.gura.MainActivity;
import com.e.gura.R;
import com.e.gura.adapters.CartAdapter;
import com.e.gura.adapters.ProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

public class Cart extends Fragment {
    public Context ctx;
    public Helper helper;
    public RecyclerView recyclerView;
    public LinearLayoutManager layoutManager;
    public RequestQueue mQueue;
    public JSONArray searchedProductsArray,allProductArray;
    public RotateLoading rotateLoading;
    public FloatingActionButton fab;
    public Cart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carting, container, false);
        ctx = view.getContext();
        helper = new Helper(ctx);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        fab = view.findViewById(R.id.checkout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Browser.class);
                intent.putExtra("url","https://mobile.e-gura.com/main/view.php?andr_final_cart&user_id="+helper.getUserId());
                startActivity(intent);
            }
        });
        rotateLoading = view.findViewById(R.id.loading);
        rotateLoading.start();
        if(helper.isLoggedIn())
            loadCart();
        else{
            Toast.makeText(ctx,"You should login first",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ctx, MainActivity.class);
            intent.putExtra("cart","go to cart");
            startActivity(intent);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        final EditText edtSearch = activity.findViewById(R.id.edtKeyword);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>1) searchInArray(s+"");
                else if(s.toString().length() == 0) setLoadedProducts("all");
            }
        });
    }

    private void searchInArray(String keyword) {
        searchedProductsArray = new JSONArray();
        if (allProductArray.length() == 0)
            Snackbar.make(recyclerView, "No products found", Snackbar.LENGTH_LONG).show();
        else {
            Log.d("all prod array", allProductArray.toString());
            for (int i = 0; i < allProductArray.length(); i++) {
                try {
                    JSONObject obj = allProductArray.getJSONObject(i);
                    if (obj.getString("product_name").toLowerCase().contains(keyword.toLowerCase()) || obj.getString("product_descr").toLowerCase().contains(keyword))
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

        if (loadType.equals("all")) dataArray = allProductArray;
        else if (loadType.equals("search")) dataArray = searchedProductsArray;
        Log.d("display data ", loadType + " - " + dataArray.toString());

        CartAdapter productAdapter = new CartAdapter(ctx, dataArray);
        recyclerView.setAdapter(productAdapter);
    }

    public void loadCart() {

        String url = "https://mobile.e-gura.com/main/view.php?andr_my_cart&user_id="+helper.getUserId();
        Log.d("cart url",url);
        mQueue = Volley.newRequestQueue(ctx);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("cart response", response.toString());

                        if(rotateLoading.isStart()) rotateLoading.stop();

                        if (response.equals(""))
                            Toast.makeText(ctx, "No Cart added", Toast.LENGTH_LONG).show();
                        else {
                            try {
                                allProductArray = new JSONArray(response);
                                CartAdapter adapter = new CartAdapter(ctx,allProductArray);
                                recyclerView.setAdapter(adapter);

                                //JSONObject response = new JSONObject(res.replace("\\", ""));

                            } catch (JSONException e) {
                                Log.d("JSON Error", e.getMessage());
                                e.printStackTrace();
                            }
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
}