package com.e.gura.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.e.gura.Helper;
import com.e.gura.R;
import com.e.gura.pages.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HorizontalCategoryAdapter extends RecyclerView.Adapter<HorizontalCategoryAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    public Helper helper;
    public JSONObject readStatusSymbol = new JSONObject();
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public HorizontalCategoryAdapter(Context context, JSONArray myDataset) {
        mDataset = myDataset;
        ctx = context;
        helper = new Helper(ctx);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HorizontalCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_product_category, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.tvCategoryName.setText(currentObj.getString("cat_name"));
            holder.tvCategoryId.setText(currentObj.getString("cat_id"));
            //set image icons
            if(position%2 != 0) holder.imgCategoryIcon.setImageDrawable(ctx.getDrawable(R.mipmap.black_product_icon));
            else holder.imgCategoryIcon.setImageDrawable(ctx.getDrawable(R.mipmap.product_icon));
           // holder.imgCategoryIcon.setImageBitmap(null);
            holder.imgCategoryIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, Home.class);
                    intent.putExtra("category",holder.tvCategoryId.getText().toString());
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
        public TextView tvCategoryName,tvCategoryId;
        public ImageView imgCategoryIcon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.singleProductHolder1);
            imgCategoryIcon = lny.findViewById(R.id.cateIcon);
            tvCategoryName = lny.findViewById(R.id.cateName);
            tvCategoryId = lny.findViewById(R.id.cateId);
            //tvMsg = lny.findViewById(R.id.tvRecyclerDate);
        }
    }
}
