package com.example.application2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends Activity {

    private ImagePagerAdapter imagePagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        viewPager = (ViewPager) findViewById(R.id.view);
        imagePagerAdapter = new ImagePagerAdapter(getApplicationContext());
        viewPager.setAdapter(imagePagerAdapter);

        Intent receivedIntent = getIntent();
        int imageID = (Integer)receivedIntent.getExtras().get("image ID");
        String imageURI = (String)receivedIntent.getExtras().get("image URI");
        if (imageID != 0)
            viewPager.setCurrentItem(whereis(imagePagerAdapter.imagelist, imageID));
        else if (!imageURI.equals(""))
            viewPager.setCurrentItem(whereis(imagePagerAdapter.imagelist, imageURI));
    }

    private class ImagePagerAdapter extends PagerAdapter{

        List<Imagelist> imagelist;
        private LayoutInflater inflater;
        Context context;

        ImagePagerAdapter(Context context){
            this.context = context;
            Intent intent = getIntent();
            this.imagelist = new ArrayList<Imagelist>();
            this.imagelist = intent.getParcelableArrayListExtra("image List");
        }

        @Override
        public int getCount() {
            return imagelist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            View v = inflater.inflate(R.layout.clickview, container, false);
            ImageView imageView = (ImageView)v.findViewById(R.id.imageviewrecycle);

            Imagelist tmp = imagelist.get(position);
            if(tmp.imageId != 0){
                imageView.setImageResource(tmp.imageId);
            }
            else if(tmp.imageId == 0){
                imageView.setImageURI(Uri.parse(tmp.imageUri));
            }

            ((ViewPager) container).addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.invalidate();
        }

    }

    public int whereis(List<Imagelist> list, int imageId) {
        for (int i=0; i<list.size(); i++) {
            Imagelist tmp = list.get(i);
            if (tmp.imageId == imageId)
                return i;
        }
        return -1;
    }

    public int whereis(List<Imagelist> list, String imageUri) {
        for (int i=0; i<list.size(); i++) {
            Imagelist tmp = list.get(i);
            if (tmp.imageUri.equals(imageUri))
                return i;
        }
        return -1;
    }
}