package com.e.gura.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DummyData {
    static JSONArray products = new JSONArray(),
                    categories = new JSONArray(),
                    cart = new JSONArray();


    public static JSONArray getProductsDummies(){
        String val = "{'product_id':'0','product_name':'','product_cat':'','product_qnty':'0','product_price':'0','product_chipping':'0','product_descr':'','product_file':'','product_file_2':'','product_file_3':'','product_adder':'2','product_color':'','product_sex':'','product_size':'','product_status':'E','product_date':'','product_category':''}";

        for (int i=1;i<=15;i++){
            try {
                products.put(new JSONObject(val));
            }catch (JSONException ex){ex.printStackTrace();};
        }
        return products;
    }
    public static JSONArray getCategoryDummies(){
        String val = "{'cat_id':'0','cat_name':'','cat_icon':'','cat_status':'','cat_date':''}";

        for (int i=1;i<=15;i++){
            try {
                categories.put(new JSONObject(val));
            }catch (JSONException ex){ex.printStackTrace();};
        }
        return categories;
    }
    public static JSONArray getCartDummies(){
        String val = "{'cart_id':'0','cart_product':'','cart_quantity':'0','cart_user':'','cart_date':'','cart_status':'','product_id':'','product_name':' ','product_cat':'','product_qnty':'0','product_price':'0','product_chipping':'','product_descr':'','product_file':'','product_file_2':'','product_file_3':'','product_adder':'','product_color':'','product_sex':'','product_size':'','product_status':'','product_date':''}";
        for (int i=1;i<=15;i++){
            try {
                cart.put(new JSONObject(val));
            }catch (JSONException ex){ex.printStackTrace();};
        }
        return cart;
    }
    public static String[] getSlider(){
        String[] arr= new String[]{"0","0","0"};
        return arr;
    }


}
