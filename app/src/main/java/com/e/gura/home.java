package com.e.gura;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class home extends AppCompatActivity {
    private TextView mTextViewResult, tvNoInternet, loading;
    private RequestQueue mQueue;
    private ProgressDialog progressBar;
    private GridLayout gridLayout;
    public ImageView img, imgUpload;
    public ArrayList arr;
    private Button btnProfile, btnSearch, loadMore;
    private EditText edtSearch;
    private Helper helper;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout relativeLayoutGoToProfile, notificationBell, rltvLayoutLogo;
    private JSONArray productsArray, searchedProductsArray, allProductArray;
    private GifImageView imgLoading;
    public int len, lastCount = 0, defaultCount = 75, loopStop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTextViewResult = findViewById(R.id.text_view_result);
        gridLayout = findViewById(R.id.grdView);
        arr = new ArrayList();
        helper = new Helper(home.this);
        imgLoading = findViewById(R.id.imgLoading);
        rltvLayoutLogo = findViewById(R.id.rltLayoutLogo);

        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.edtSearch);
        tvNoInternet = findViewById(R.id.tvNoInternet);
        loadMore = findViewById(R.id.loadMore);
       // loading = findViewById(R.id.loading);
        imgUpload = findViewById(R.id.imgUpload);
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (helper.getUserId().equals("0")) {
                    intent = new Intent(home.this, MainActivity.class);
                    intent.putExtra("upload", "product");
                    Toast.makeText(home.this,"You should login to continue",Toast.LENGTH_LONG).show();
                } else {
                    intent = new Intent(home.this, UploadProduct.class);
                }
                startActivity(intent);
            }
        });
        //show & hide textview no internet
        helper.toggleNetworkConnectivityTextView(tvNoInternet);

        recyclerView = findViewById(R.id.recyclerView);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadedProducts("all");
            }
        });
        relativeLayoutGoToProfile = findViewById(R.id.relativeLayoutGoToProfile);
        notificationBell = findViewById(R.id.notificationBell);

        relativeLayoutGoToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (helper.getUserId().equals("0")) {
                    intent = new Intent(home.this, MainActivity.class);
                    intent.putExtra("profile", "go to profile");
                    Toast.makeText(home.this,"You should login to continue",Toast.LENGTH_LONG).show();
                } else {
                    intent = new Intent(home.this, Profile.class);
                }
                startActivity(intent);
            }
        });
        rltvLayoutLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("category")) finish();
            }
        });
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mQueue = Volley.newRequestQueue(this);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Loading products...");
        progressBar.setCancelable(true);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helper.isNetworkConnected()) {
                    searchInArray();
                        /*setLoadedProducts("all");
                    else
                        */

                    //setProductsUrl("search", "0");
                } else
                    Toast.makeText(home.this, "You don't have internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadCategories();
            }
        });
        t.start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.toggleNetworkConnectivityTextView(tvNoInternet);
    }

    public int progressCounter() {
        return 100 * (lastCount / loopStop);
    }

    public void loadProfile() {
        startActivity(new Intent(home.this, Profile.class));
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
                url = "https://e-gura.com/js/ajax/main.php?andr_prod_search&content=" + edtSearch.getText().toString().trim().replaceAll(" ", "%20");
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
                                Toast.makeText(home.this, "No products found", Toast.LENGTH_LONG).show();
                                imgLoading.setVisibility(View.GONE);
                            }
                            if (!getIntent().hasExtra("category")) allProductArray = productsArray;
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
        Log.d("prod to search in","Query runned");
        String localUrl = "https://e-gura.com/main/view.php?and_products_imgs";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, localUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("prod to search in resp",response.toString());
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
            Snackbar.make(edtSearch, "No products found", Snackbar.LENGTH_LONG).show();
        else {
            Log.d("all prod array",allProductArray.toString());
            for (int i = 0; i < allProductArray.length(); i++) {
                try {
                    JSONObject obj = allProductArray.getJSONObject(i);
                    if (obj.getString("product_name").toLowerCase().contains(edtSearch.getText().toString().toLowerCase()) || obj.getString("product_descr").toLowerCase().contains(edtSearch.getText().toString().toLowerCase()))
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

        if (dataArray.length() == 0) {
            Snackbar.make(edtSearch, "No products found", Snackbar.LENGTH_LONG).show();
        } else {
            try {
                gridLayout.removeAllViews();
                //adapter on gridview
                while (lastCount < loopStop) {
                    JSONObject product = dataArray.getJSONObject(lastCount);
                    //add data to adapter
                    //gridAdapterData.put(product);

                    String product_name = product.getString("product_name");
                    String product_price = product.getString("product_price");
                    String product_descr = product.getString("product_descr");
                    String product_file = product.getString("product_file");
                    TextView tv = new TextView(home.this);
                    //img.setImageBitmap(getImageBitmap(product_file));
                    //new Background().execute(new String[]{product_file, product_name, product_price,product.toString(), String.valueOf(loopStop - (lastCount + 1))});

                    //Background tasks
                    Display display = getWindowManager().getDefaultDisplay();
                    int dispWidth = display.getWidth() / 3, dispHeight = display.getHeight() / 3;
                    int width = 230, height = 230;
                    ImageView imgX = new ImageView(home.this);
                    TextView caption = new TextView(home.this);
                    TextView captionPrice = new TextView(home.this);
                    LinearLayout lny = new LinearLayout(home.this);
                    final Intent intent = new Intent(home.this, ProductInfo.class);
                    intent.putExtra("product", product.toString());

                    lny.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dispWidth, height),
                            textviewParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

                    lny.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    lny.setPadding(6, 3, 6, 3);
                    caption.setLayoutParams(textviewParams);
                    caption.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    caption.setText(product_name);
                    caption.setGravity(Gravity.CENTER_HORIZONTAL);
                    captionPrice.setLayoutParams(textviewParams);
                    captionPrice.setTextColor(getResources().getColor(R.color.green));
                    captionPrice.setText("(" + product_price + " RWF)");
                    captionPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    captionPrice.setGravity(Gravity.CENTER_HORIZONTAL);
                    captionPrice.setTextSize(16);
                    imgX.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //view product all information
                            startActivity(intent);
                        }
                    });
                    caption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //view all product information
                            startActivity(intent);
                        }
                    });

                    imgX.setLayoutParams(params);
                    //imgX.setImageBitmap(feed);

                    Glide.with(home.this)
                            .load(product_file)
                            .placeholder(R.drawable.logo_egura) //placeholder
                            .centerCrop()
                            .error(R.drawable.logo_egura) //error
                            .into(imgX);

                    lny.addView(imgX);
                    lny.addView(caption);
                    lny.addView(captionPrice);
                    //lny.addView(captionPrice);
                    int countChild = gridLayout.getChildCount();
                    gridLayout.addView(lny, countChild);
                    // gridLayout.addView(lny);
                    imgLoading.setVisibility(View.GONE);
                    gridLayout.refreshDrawableState();

                    //End background tasks
                    tv.setText(product_name + ", " + product_price + ", " + product_descr + ", " + product_file);

                    //Increment last count to enable pagination
                    lastCount++;
                }

            } catch (JSONException ex) {
                Log.d("jsonerr", ex.getMessage());
            }
        }
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
                            if (getIntent().hasExtra("category"))
                                setProductsUrl("product_based_category", getIntent().getStringExtra("category"));
                            else setProductsUrl("all", "0");

                            //set products' category to recyclerview
                            adapter = new MyAdapter(home.this, response.getJSONArray("all_categories"));
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

    private void searchProduct() {
        try {
            JSONObject product = productsArray.getJSONObject(lastCount);
            //add data to adapter
            //gridAdapterData.put(product);

            String product_name = product.getString("product_name");
            String product_price = product.getString("product_price");
            String product_descr = product.getString("product_descr");
            String product_file = product.getString("product_file");
            TextView tv = new TextView(home.this);
            //img.setImageBitmap(getImageBitmap(product_file));
            //new Background().execute(new String[]{product_file, product_name, product_price,product.toString(), String.valueOf(loopStop - (lastCount + 1))});

            //Background tasks
            Display display = getWindowManager().getDefaultDisplay();
            int dispWidth = display.getWidth() / 3, dispHeight = display.getHeight() / 3;
            int width = 230, height = 230;
            ImageView imgX = new ImageView(home.this);
            TextView caption = new TextView(home.this);
            TextView captionPrice = new TextView(home.this);
            LinearLayout lny = new LinearLayout(home.this);
            final Intent intent = new Intent(home.this, ProductInfo.class);
            intent.putExtra("product", product.toString());

            lny.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dispWidth, height),
                    textviewParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

            lny.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            caption.setLayoutParams(textviewParams);
            caption.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            caption.setText(product_name);
            caption.setGravity(Gravity.CENTER_HORIZONTAL);
            captionPrice.setLayoutParams(textviewParams);
            captionPrice.setTextColor(getResources().getColor(R.color.green));
            captionPrice.setText("(" + product_price + " RWF)");
            captionPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            captionPrice.setGravity(Gravity.CENTER_HORIZONTAL);
            captionPrice.setTextSize(16);
            imgX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //view product all information
                    startActivity(intent);
                }
            });
            caption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //view all product information
                    startActivity(intent);
                }
            });

            imgX.setLayoutParams(params);
            //imgX.setImageBitmap(feed);

            Glide.with(home.this)
                    .load(product_file)
                    .placeholder(R.drawable.logo_egura) //placeholder
                    .centerCrop()
                    .error(R.drawable.logo_egura) //error
                    .into(imgX);

            lny.addView(imgX);
            lny.addView(caption);
            lny.addView(captionPrice);
            //lny.addView(captionPrice);
            int countChild = gridLayout.getChildCount();
            gridLayout.addView(lny, countChild);
            // gridLayout.addView(lny);
            imgLoading.setVisibility(View.GONE);
            gridLayout.refreshDrawableState();

            //End background tasks
            tv.setText(product_name + ", " + product_price + ", " + product_descr + ", " + product_file);

            //Increment last count to enable pagination
            lastCount++;
                                /*GridviewAdapter adapter = new GridviewAdapter(home.this,gridAdapterData);
                                gridLayout.setAdapter(adapter);*/

            progressBar.dismiss();
        } catch (JSONException e) {
            Log.d("JSON Error", e.getMessage());
            e.printStackTrace();
        }
    }
}
