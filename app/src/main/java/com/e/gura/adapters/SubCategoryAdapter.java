package com.e.gura.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.gura.Helper;
import com.e.gura.Navigator;
import com.e.gura.R;
import com.e.gura.pages.Home;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    public Helper helper;
    public JSONObject readStatusSymbol = new JSONObject();
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SubCategoryAdapter(Context context, JSONArray myDataset) {
        mDataset = myDataset;
        ctx = context;
        helper = new Helper(ctx);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SubCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_categories, parent, false);
        SubCategoryAdapter.MyViewHolder vh = new SubCategoryAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final SubCategoryAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.tvCategoryName.setText(currentObj.getString("sub_cat_name"));
            holder.tvCategoryId.setText(currentObj.getString("sub_cat_id"));
            //set image icons
//            if(position%2 != 0) holder.imgCategoryIcon.setImageDrawable(ctx.getDrawable(R.mipmap.black_product_icon));
//            else holder.imgCategoryIcon.setImageDrawable(ctx.getDrawable(R.mipmap.product_icon));
            //set image icons
            Glide.with(ctx)
                    .load("https://mobile.e-gura.com/img/categories/"+currentObj.getString("cat_icon"))
                    .placeholder(R.drawable.ic_baseline_image_24) //placeholder
                    .centerCrop()
                    .error(R.drawable.ic_baseline_image_24) //error
                    .into(holder.imgCategoryIcon);
            holder.tvCategoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ClickDebug","Debug in adapter");
                    Toast.makeText(ctx,"Opening modal",Toast.LENGTH_SHORT).show();
                }
            });
            // holder.imgCategoryIcon.setImageBitmap(null);
            holder.lnlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ctx,"Open filter",Toast.LENGTH_SHORT).show();
                    Log.d("ClickDebug","Debug in adapter");
                    Intent intent = new Intent(ctx, Navigator.class);
                    intent.putExtra("subcategory",holder.tvCategoryId.getText().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);

                    //  Toast.makeText(ctx,"Category "+holder.tvCategoryName.getText().toString()+" - Id "+holder.tvCategoryId.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });
            holder.lnlayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("ClickDebug","Layout touched");
                    Toast.makeText(ctx,"Touched",Toast.LENGTH_SHORT).show();
                    return false;
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
        public TextView tvCategoryName,tvCategoryId;
        public CircularImageView imgCategoryIcon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.lnySubcategory);
            imgCategoryIcon = lny.findViewById(R.id.categoryIcon);
            tvCategoryName = lny.findViewById(R.id.categoryName);
            tvCategoryId = lny.findViewById(R.id.categoryId);
            //tvMsg = lny.findViewById(R.id.tvRecyclerDate);
        }
    }
}

