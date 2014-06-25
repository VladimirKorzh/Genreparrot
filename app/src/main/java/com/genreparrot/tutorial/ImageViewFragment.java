package com.genreparrot.tutorial;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.genreparrot.app.R;

public class ImageViewFragment extends Fragment {

    private Integer itemData;
    private Bitmap myBitmap;
    private ImageView ivImage;

    public static ImageViewFragment newInstance() {
        ImageViewFragment f = new ImageViewFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.single_image, container, false);
        ivImage = (ImageView) root.findViewById(R.id.ivImageView);
        setImageInViewPager();
        return root;
    }
    public void setImageList(Integer integer) {
        this.itemData = integer;
    }
    public void setImageInViewPager() {
        myBitmap = BitmapFactory.decodeResource(getResources(), itemData);
        if (myBitmap != null) {
            try {
                if (ivImage != null) {
                    ivImage.setImageBitmap(myBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myBitmap != null) {
            myBitmap.recycle();
            myBitmap = null;
        }
    }
}