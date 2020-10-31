package com.e.gura.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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

import com.bumptech.glide.Glide;
import com.e.gura.Helper;
import com.e.gura.R;
import com.e.gura.pages.Home;
import com.e.gura.pages.Subcategory;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
    public void onBindViewHolder(final CartAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            final JSONObject currentObj = mDataset.getJSONObject(position);
            final String product = currentObj.getString("product_name");
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.counter.setText(String.valueOf(position+1));
            holder.tvProductName.setText(currentObj.getString("product_name"));
            holder.tvProductQuantity.setText(currentObj.getString("cart_quantity"));
            holder.tvProductPu.setText(currentObj.getString("product_price"));
            holder.total.setText((Integer.parseInt(currentObj.getString("product_quantity"))*Integer.parseInt(currentObj.getString("product_price"))));
            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //confirm removal of product
                    AlertDialog dialog = new AlertDialog.Builder(ctx).create();
                    dialog.setTitle("Confirm cart product removal");
                    dialog.setMessage("Tap remove to confirm removal of "+product+" from cart");
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //send request to remove from cart
                            Toast.makeText(ctx,"Remove product request",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            //set image icons
//            Glide.with(ctx)
//                    .load("https://e-gura.com/images/products/"+currentObj.getString("cat_icon"))
//                    .placeholder(R.drawable.logo_egura) //placeholder
//                    .centerCrop()
//                    .error(R.drawable.logo_egura) //error
//                    .into(holder.imgCartIcon);
//            // holder.imgCartIcon.setImageBitmap(null);
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
        public TextView counter,tvProductName,tvProductQuantity,tvProductPu,total;
        public Button btnAction;
        public CircularImageView imgCartIcon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            counter = lny.findViewById(R.id.tvCounter);
            tvProductName = lny.findViewById(R.id.prodName);
            tvProductQuantity = lny.findViewById(R.id.productQuantity);
            tvProductPu = lny.findViewById(R.id.productPu);
            total = lny.findViewById(R.id.total);
            btnAction = lny.findViewById(R.id.actions);
        }
    }
}

