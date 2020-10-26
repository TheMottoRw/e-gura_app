package com.e.gura.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.gura.Helper;
import com.e.gura.ProductInfo;
import com.e.gura.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    public Helper helper;
    public JSONObject readStatusSymbol = new JSONObject();
    private JSONArray mDataset;
    private GridLayout gridLayout;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductAdapter(Context context, JSONArray myDataset) {
        mDataset = myDataset;
        ctx = context;
        helper = new Helper(ctx);
//        gridLayout = grdLayout;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_products, parent, false);
        ProductAdapter.MyViewHolder vh = new ProductAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ProductAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            final JSONObject currentObj = mDataset.getJSONObject(position);
            String productDescription = currentObj.getString("product_descr").length()>16?currentObj.getString("product_descr").replaceAll("\r\n"," ").replaceAll("\n"," ").substring(0,14)+"...":currentObj.getString("product_descr");
            String productQuantity = currentObj.getString("product_qnty");
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.tvProductId.setText(currentObj.getString("product_id"));
            holder.tvProductName.setText(currentObj.getString("product_name"));
            holder.tvProductDescription.setText(productDescription);
            holder.tvProductPrice.setText("RWF "+currentObj.getString("product_price"));
            holder.tvProductSoldQuantity.setText(getSoldQuantity(Integer.parseInt(productQuantity))+" sold in "+productQuantity);
            //set image icons
            Glide.with(ctx)
                    .load(currentObj.getString("product_file"))
                    .placeholder(R.drawable.logo_egura) //placeholder
                    .centerCrop()
                    .error(R.drawable.logo_egura) //error
                        .into(holder.imageIcon);
            // holder.imgCategoryIcon.setImageBitmap(null);
            holder.imageIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, ProductInfo.class);
                    intent.putExtra("product",currentObj.toString());
                    ctx.startActivity(intent);

                    //  Toast.makeText(ctx,"Category "+holder.tvCategoryName.getText().toString()+" - Id "+holder.tvCategoryId.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });

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
        public TextView tvProductId,tvProductName,tvProductDescription,tvProductPrice,tvProductSoldQuantity;
        public ImageView imageIcon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.lnyProductItem);
            imageIcon = lny.findViewById(R.id.imgx);
            tvProductId = lny.findViewById(R.id.tvProductId);
            tvProductName = lny.findViewById(R.id.tvProductName);
            tvProductDescription = lny.findViewById(R.id.tvProductDescription);
            tvProductPrice = lny.findViewById(R.id.tvProductPrice);
            tvProductSoldQuantity = lny.findViewById(R.id.tvProductSoldQuantity);
        }
    }
    public static int getSoldQuantity(int soldQuantity) {
        int max = Integer.parseInt(String.valueOf(soldQuantity/2));
        int min = Integer.parseInt(String.valueOf(soldQuantity/10));
        if (min > max) {
            throw new IllegalArgumentException("Min " + min + " greater than max " + max);
        }
        return (int) ( (long) min + Math.random() * ((long)max - min + 1));
    }
}


