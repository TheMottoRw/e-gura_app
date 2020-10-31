package com.e.gura;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadProduct extends AppCompatActivity {
    ProgressDialog prgDialog;
    private Helper helper;
    String encodedString;
    HashMap params = new HashMap<String, String>();
    String imgPath, fileName;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;
    private EditText edtName, edtPu, edtQty, edtShippingfee, edtDescr, edtColor, edtSize;
    private Spinner spnCategory;
    private TextView tvNoInternet;
    private Button btnImages, btnUpload;
    private RadioButton radMale, radFemale,radBoth;
    private ArrayList dataName = new ArrayList(), dataId = new ArrayList();

    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    List path = new ArrayList();
    private String encodedImage, encodedImage1, encodeImage2;

    //redesign feature
    private ImageView goBack;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);
        helper = new Helper(UploadProduct.this);
        gvGallery = findViewById(R.id.grdView);
        prgDialog = new ProgressDialog(UploadProduct.this);
        tvNoInternet = findViewById(R.id.tvNoInternet);

        edtName = findViewById(R.id.edtProdName);
        edtPu = findViewById(R.id.edtPu);
        edtQty = findViewById(R.id.edtQty);
        edtShippingfee = findViewById(R.id.edtShippingFee);
        edtDescr = findViewById(R.id.edtDescr);
        edtColor = findViewById(R.id.edtColor);
        edtSize = findViewById(R.id.edtSize);
        spnCategory = findViewById(R.id.productCategory);
        radMale = findViewById(R.id.radMale);
        radFemale = findViewById(R.id.radFemale);
        radBoth = findViewById(R.id.radBoth);
        btnImages = findViewById(R.id.btnImages);
        btnUpload = findViewById(R.id.btnUpload);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery(v);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate product
                if(validateProductDetails())
                triggerImageUpload();
            }
        });
        helper.toggleNetworkConnectivityTextView(tvNoInternet);
        //check if user exist
        checkAuthorization();
        //loading categories to spinner
        loadCategories();
    }
    @Override
    protected void onResume(){
        super.onResume();
        helper.toggleNetworkConnectivityTextView(tvNoInternet);
    }
    void checkAuthorization(){
    if (!helper.isLoggedIn()) {
        Toast.makeText(UploadProduct.this, "You should log in to continue", Toast.LENGTH_LONG).show();
        finish();
        Intent intent = new Intent(UploadProduct.this, MainActivity.class);
        intent.putExtra("upload", "product");
        startActivity(intent);
    }
    }

    private boolean validateProductDetails(){
        String message = "";
        boolean isValid = true;
        if(dataId.get(spnCategory.getSelectedItemPosition()).toString().equals("0"))
            message = "You must select product category";
        else if(edtName.getText().toString().trim().equals(""))
            message = "Product name shoud not be empty";
        else if(edtPu.getText().toString().trim().equals("") || Integer.parseInt(edtPu.getText().toString().trim()) <= 0)
            message  = "Price should be a number greater than zero";
        else if(edtDescr.getText().toString().equals(""))
            message = "Please add short description of product";
        else if(edtQty.getText().toString().trim().equals("") || Integer.parseInt(edtQty.getText().toString().trim()) <= 0)
            message = "Quantity should be a number greater than zero";
        else if (path.size() <3 )
            message = "You should select 3 images to describe your product";
        else if(path.size() >3)
            message = "Only 3 Images needed to describe your product";
        if(!message.equals("")){
            isValid = false;
            Snackbar.make(spnCategory,message,Snackbar.LENGTH_LONG).show();
        }
        return isValid;
    }
    private void loadCategories() {
        String url = "https://e-gura.com/js/ajax/main.php?all_products_categories";
        RequestQueue mQueue = Volley.newRequestQueue(UploadProduct.this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        //set products' category to spinner
                        setLoadedProductCategories(response);
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

    void setLoadedProductCategories(JSONObject obj) {
        try {
            dataId.add("0");
            dataName.add("Select category");
            JSONArray arr = obj.getJSONArray("all_categories");
            for (int len = 0; len < arr.length(); len++) {
                dataId.add(len + 1, arr.getJSONObject(len).getString("cat_id"));
                dataName.add(len + 1, arr.getJSONObject(len).getString("cat_name"));
            }
            ArrayAdapter adapter = new ArrayAdapter(UploadProduct.this, android.R.layout.simple_spinner_item, dataName);
            spnCategory.setAdapter(adapter);
        } catch (JSONException e) {
            Log.d("JSON Error", e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadImagefromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);//= new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setType("image/*");

        startActivityForResult(Intent.createChooser(galleryIntent, "Select 3 products image"), RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    Log.d("Image uri", mImageUri.toString());

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri.add(mImageUri);
                    Log.d("Image uri", mArrayUri.toString());
                    galleryAdapter = new GalleryAdapter(UploadProduct.this, mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(UploadProduct.this, mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        Log.v("LOG_IMAGE_URIs", mArrayUri.toString());

                    }
                }
                //get image path and convert into encoded string
                getPath(mArrayUri);
                uploadImage(gvGallery);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("choose image gallery", e.getMessage());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //method to get the file path from uri
    public List getPath(ArrayList<Uri> uri) {
        path = new ArrayList();
        for (int i = 0; i < uri.size(); i++) {
            Cursor cursor = getContentResolver().query(uri.get(i), null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            cursor.close();
        }
        return path;
    }

    // When Upload button is clicked


    public void uploadImage(View v) {
        // When Image is selected from Gallery
        Log.d("Image counts", "Selected Images " + path.size());
        if (path.size() != 0) {
           // prgDialog.setMessage("Converting Image to Binary Data");
            //prgDialog.show();
            // Convert image to String using Base64
            encodeImagetoString();
            // When Image is not selected from Gallery
        } else {
            Toast.makeText(
                    UploadProduct.this,
                    "You must select image from gallery before you try to upload",
                    Toast.LENGTH_LONG).show();
        }

    }


    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }

            ;

            @Override

            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                for (int i = 0; i < path.size(); i++) {
                    bitmap = BitmapFactory.decodeFile(path.get(i).toString(),
                            options);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                    byte[] byte_arr = stream.toByteArray();
                    if (i == 0) encodedImage = Base64.encodeToString(byte_arr, 0);
                    else if (i == 1) encodedImage1 = Base64.encodeToString(byte_arr, 0);
                    else encodeImage2 = Base64.encodeToString(byte_arr, 0);
                }
                return "";
            }

            @Override

            protected void onPostExecute(String msg) {
                prgDialog.dismiss();
                //Toast.makeText(UploadProduct.this,"Image decoded", Toast.LENGTH_SHORT).show();
                if (path.size() !=3){
                    AlertDialog alert = new AlertDialog.Builder(UploadProduct.this).create();
                    alert.setMessage("You must select 3 images which describe your product");
                    alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        if(helper.isNetworkConnected())
                requestHandlerUpload();
        else Toast.makeText(UploadProduct.this,"You don't have internet",Toast.LENGTH_SHORT).show();
    }

    private void requestHandlerUpload(){
        class UploadImage extends AsyncTask<Void,Void,String>{
            String url = "https://e-gura.com/js/ajax/main.php?andr_sys_prod_add";
            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UploadProduct.this, "Uploading product...", null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(UploadProduct.this,"Product uploaded successful",Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... pars) {
                HashMap<String, String> params = new HashMap<>();
                params.put("pr_cat", dataId.get(spnCategory.getSelectedItemPosition()).toString());
                params.put("pr_name", edtName.getText().toString());
                params.put("pr_price", edtPu.getText().toString());
                params.put("pr_qnty", edtQty.getText().toString());
                params.put("pr_desc", edtDescr.getText().toString());
                params.put("pr_ship", edtShippingfee.getText().toString());
                params.put("pr_color", edtColor.getText().toString());
                params.put("pr_size", edtSize.getText().toString());
                params.put("pr_sex", radMale.isChecked() ? "Male" : radFemale.isChecked()?"Female":"None");
                params.put("pro_pic", encodedImage);
                params.put("pro_pic_2", encodedImage1);
                params.put("pro_pic_3", encodeImage2);
                params.put("user_id", helper.getUserId());
                String result = rh.sendPostRequest(url,params);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }

}
