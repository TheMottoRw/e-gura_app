package com.e.gura;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class GridviewAdapter extends BaseAdapter {
    private Context ctx;
    private JSONArray mData;
    ImageView imgX;
    private int length = 9,start = 0, end = 0;

    public GridviewAdapter(Context context, JSONArray array) {
        this.ctx = context;
        this.mData = array;
        this.start = start;
        this.end = end;
    }
    @Override
    public int getCount() {
        return mData.length();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public LinearLayout getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        String imgUrl = "";
        Log.d("Adapter grid","Started getview");
        DisplayMetrics display  = ctx.getResources().getDisplayMetrics();
        int dispWidth = display.widthPixels/3,dispHeight = display.heightPixels/3;
        int width = 230, height = 230;
        imgX = new ImageView(ctx);
        TextView caption = new TextView(ctx);
        TextView captionPrice = new TextView(ctx);
        LinearLayout lny = new LinearLayout(ctx);

        final Intent intent = new Intent(ctx, ProductInfo.class);

        lny.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dispWidth, height),
                textviewParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lny.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        caption.setLayoutParams(textviewParams);
        caption.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        captionPrice.setLayoutParams(textviewParams);
        captionPrice.setTextColor(ctx.getResources().getColor(R.color.green));

        captionPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        captionPrice.setTextSize(16);

        try {
            JSONObject obj = mData.getJSONObject(position);
            imgUrl = obj.getString("product_file");
            caption.setText(obj.getString("product_name"));
            captionPrice.setText(mData.getJSONObject(position).getString("product_price")+" RWF");
            caption.setText("productname");
        }catch (JSONException ex){
            Log.e("Json adapter",ex.getMessage());
        }
        //captionPrice.setText("(productprice RWF)");
        imgX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view product all information
                ctx.startActivity(intent);
            }
        });
        caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view all product information
                ctx.startActivity(intent);
            }
        });

        imgX.setLayoutParams(params);
        imgX.setImageBitmap(null);
        new Background().execute(new String[]{imgUrl});
        lny.addView(imgX);
        lny.addView(caption);
        lny.addView(captionPrice);

        /*if (convertView == null) {
            imageView = new ImageView(ctx);
            DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
            width = metrics.widthPixels / 3;
            height = metrics.heightPixels / 3;
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(R.drawable.logo);*/
        return lny;
    }


    class Background extends AsyncTask<String, Void, Bitmap> {
        private Bitmap bm;
        private InputStream is;
        private BufferedInputStream bis;
        private Exception exception;
        private String productname, productprice,products;

        protected Bitmap doInBackground(String... urls) {
            try {
                URL aURL = new URL(urls[0]);
                /*productname = urls[1];
                productprice = urls[2];
                products = urls[3];*/
                URLConnection conn = aURL.openConnection();
                conn.connect();
                is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                //Fixing out of memory

                //Decode image size
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;
                String imageType = options.outMimeType;

//                BitmapFactory.decodeStream(bis,null, options);

                //decode with insample size
                options.inSampleSize = calculateInSampleSize(options, 48, 36);
                options.inJustDecodeBounds = false;
                // Decode bitmap with inSampleSize se
                bm = BitmapFactory.decodeStream(bis, null, options);

                return bm;
            } catch (Exception e) {
                this.exception = e;

                return null;
            } finally {
                // if(Integer.parseInt(urls[3]) == 1 ) loading.setVisibility(View.GONE);
            }
        }

        protected void onPostExecute(Bitmap feed) {
            Log.d("Adapter post execute","Background Post executed");

            //lny.addView(captionPrice);
            imgX.setImageBitmap(feed);
            //hide loading textview
            /*if (loopStop == lastCount) {
                imgLoading.setVisibility(View.GONE);
            }*/

        }

        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        private int inSampleSizeCalculator(BitmapFactory.Options o, int reqWidth, int reqHeight) {
            if (o.outHeight > reqHeight || o.outWidth > reqWidth) {
                return (int) Math.pow(2, (int) Math.ceil(Math.log(reqWidth /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }
            return 4;
        }
    }
}

