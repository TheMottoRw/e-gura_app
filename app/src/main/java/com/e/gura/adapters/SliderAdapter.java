package com.e.gura.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.e.gura.R;

public class SliderAdapter extends PagerAdapter {
    Context mContext;
    String[] imgArr;

    public SliderAdapter(Context context, String[] imgArr) {
        this.mContext = context;
        this.imgArr = imgArr;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

//    private int[] sliderImageId = new int[]{
//            R.drawable.girl_marketing, R.drawable.logo_egura, R.drawable.input,
//    };

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(mContext)
                    .load(imgArr[position])
                    .placeholder(R.drawable.ic_baseline_image_24) //placeholder
                    .centerCrop()
                    .error(R.drawable.ic_baseline_image_24) //error
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        imageView.setImageURI(Uri.parse(imgArr[position].toString()
        ));
//        imageView.setImageResource(imgArr[position]);
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return imgArr.length;
    }
}