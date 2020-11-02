package com.e.gura.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.e.gura.Helper;
import com.e.gura.MainActivity;
import com.e.gura.ProductInfo;
import com.e.gura.R;
import com.e.gura.pages.Home;
import com.e.gura.pages.Subcategory;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    public Helper helper;
    public JSONObject readStatusSymbol = new JSONObject();
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CartAdapter(Context context, JSONArray myDataset) {
        mDataset = myDataset;
        ctx = context;
        helper = new Helper(ctx);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cart, parent, false);
        CartAdapter.MyViewHolder vh = new CartAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final CartAdapter.MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            final JSONObject currentObj = mDataset.getJSONObject(position);
            final String cartId = currentObj.getString("cart_id");
            final String product = currentObj.getString("product_name");
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.counter.setText(String.valueOf(position+1));
            holder.tvProductName.setText(currentObj.getString("product_name"));
            holder.tvProductQuantity.setText(currentObj.getString("cart_quantity"));
            holder.tvProductPu.setText(currentObj.getString("product_price")+" rwf");
            int cost = Integer.parseInt(currentObj.getString("cart_quantity"))*Integer.parseInt(currentObj.getString("product_price"));
            holder.totalCost.setText(cost+" rwf");
//            holder.total.setText((Integer.parseInt(currentObj.getString("cart_quantity"))*Integer.parseInt(currentObj.getString("product_price"))));

            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //confirm removal of product
                  Toast.makeText(ctx,"Long click on delete to remove product from cart",Toast.LENGTH_LONG).show();
                }
            });
            holder.btnAction.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final ProgressDialog dialog = new ProgressDialog(ctx);
                    dialog.setProgress(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Removing product from cart");
                    dialog.setCancelable(false);
                    dialog.show();
                    String url = "https://mobile.e-gura.com/js/ajax/main.php?remove_in_cart_andr_2&cart_id="+cartId;
                    Log.d("add to cart","URL "+url);

                    RequestQueue requestQueue = Volley.newRequestQueue(ctx);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("add to cart","Response "+response);
                            dialog.dismiss();
                            if(response.trim().equals("\"success\"")) {
                                Toast.makeText(ctx, "Product removed from cart", Toast.LENGTH_LONG).show();
                                mDataset.remove(position);
                                notifyDataSetChanged();
                            }
                            else Toast.makeText(ctx,"Failed to remove product from cart "+response,Toast.LENGTH_LONG).show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
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
                    return false;
                }
            });
            //set image icons
            Glide.with(ctx)
                    .load("https://e-gura.com/images/products/"+currentObj.getString("product_file"))
                    .placeholder(R.drawable.ic_baseline_image_24) //placeholder
                    .centerCrop()
                    .error(R.drawable.ic_baseline_image_24) //error
                    .into(holder.imgCartIcon);
            // holder.imgCartIcon.setImageBitmap(null);
//            holder.imgCartIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(ctx, Subcategory.class);
//                    intent.putExtra("category",holder.tvCartId.getText().toString());
//                    intent.putExtra("category_name",holder.tvCartId.getText().toString());
//                    ctx.startActivity(intent);
//
//                    //  Toast.makeText(ctx,"Cart "+holder.tvCartName.getText().toString()+" - Id "+holder.tvCartId.getText().toString(),Toast.LENGTH_SHORT).show();
//                }
//            });

        } catch (JSONException ex) {

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView counter,tvProductName,tvProductQuantity,tvProductPu,totalCost;
        public ImageView btnAction;
        public CircularImageView imgCartIcon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            counter = lny.findViewById(R.id.tvCounter);
            imgCartIcon = lny.findViewById(R.id.productIcon);
            tvProductName = lny.findViewById(R.id.productName);
            tvProductQuantity = lny.findViewById(R.id.productQuantity);
            tvProductPu = lny.findViewById(R.id.productPu);
            totalCost = lny.findViewById(R.id.totalCost);
            btnAction = lny.findViewById(R.id.actions);
        }
    }
}

