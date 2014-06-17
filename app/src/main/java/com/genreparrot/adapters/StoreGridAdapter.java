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

public class StoreGridAdapter extends ArrayAdapter<SoundPackage> {

    private Context mContext;
    public ArrayList<SoundPackage> packages;

    public StoreGridAdapter(Context c, ArrayList<SoundPackage> values) {
        super(c, R.layout.grid_single_package_layout, values);
        mContext = c;
        packages = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.grid_single_package_layout, parent, false);

        ImageView img = (ImageView) rowView.findViewById(R.id.imgHeader);
        TextView caption = (TextView) rowView.findViewById(R.id.txtCaption);
        ImageView imgAv = (ImageView) rowView.findViewById(R.id.imgAvailable);

        SoundPackage sp = packages.get(position);
        if (sp != null) {
            // check if a package is missing a header image
            if (sp.image != null) img.setImageBitmap(sp.image);

            caption.setText(sp.props.getProperty("caption", "null"));

            if (sp.owned)
                imgAv.setImageResource(R.drawable.purchased);
        }
        return rowView;
    }
}