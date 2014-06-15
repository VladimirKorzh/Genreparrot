package com.genreparrot.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.genreparrot.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class StoreGridAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> values = new ArrayList<String>();

    public StoreGridAdapter(Context c, ArrayList values) {
        super(c, R.layout.grid_single_package_layout, values);
        mContext = c;
        this.values = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.grid_single_package_layout, parent, false);

        ImageView img = (ImageView) rowView.findViewById(R.id.imgHeader);
        TextView caption = (TextView) rowView.findViewById(R.id.txtCaption);

        // set the image of the requested package
        AssetManager assetManager = getContext().getAssets();
        InputStream istr = null;
        try {
            Bitmap bmp= BitmapFactory.decodeStream(am.open(values[position]+"/header.png"));
            img.setImageBitmap(bmp);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // set the caption
        caption.setText(values[position]);

        return rowView;
    }
}