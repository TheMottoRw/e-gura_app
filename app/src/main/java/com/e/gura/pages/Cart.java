package com.e.gura.pages;

import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.gura.Helper;
import com.e.gura.R;
import com.e.gura.adapters.CartAdapter;

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
        loadCart();
        return view;
    }

    public void loadCart() {
        // Optionally, you can just use the default CookieManager
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault( manager  );
        RequestQueue mQueue = Volley.newRequestQueue(ctx);


        String url = "https://mobile.e-gura.com/main/view.php?andr_my_cart&egura_user_id="+helper.getUserId();
        Log.d("cart url",url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("cart response", response.toString());
                        if (response.equals(""))
                            Toast.makeText(ctx, "No Cart added", Toast.LENGTH_LONG).show();
                        else {
                            try {
                                JSONArray arr = new JSONArray(response);
                                CartAdapter adapter = new CartAdapter(ctx,arr);
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