package com.genreparrot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.genreparrot.app.R;

import java.util.ArrayList;

/**
 * Created by vladimir on 6/13/2014.
 */
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



        return rowView;
    }
}