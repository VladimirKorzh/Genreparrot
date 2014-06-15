package com.genreparrot.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.genreparrot.app.R;

import java.util.ArrayList;

public class StoreGridAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> values = new ArrayList<String>();
    private static ArrayList<SoundPackage> packages = new ArrayList<SoundPackage>();


    public StoreGridAdapter(Context c, ArrayList<String> values) {
        super(c, R.layout.grid_single_package_layout, values);
        mContext = c;
        this.values = values;

        for (String file : values) {
            packages.add(new SoundPackage(mContext, file));
        }
    }


    public void purchasePackageDialog(final int position){
        final ArrayAdapter<String> ad;
        final ArrayList<String> arr;

        // get only the aliases of files
        arr = new ArrayList<String>(packages.get(position).files.values());

        ad = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_single_choice, arr);

        final SoundPackage sp = packages.get(position);

        new AlertDialog.Builder(getContext())
                .setTitle(packages.get(position).props.getProperty("caption"))
                .setSingleChoiceItems(ad, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SoundBatchPlayer.getInstance().playSingleFile(getContext(),
                                sp.files.inverse().get(arr.get(i)));
                    }
                })
                .setNegativeButton(R.string.btn_Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(R.string.btn_Purchase, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setCancelable(true)
                .show();
    }



    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.grid_single_package_layout, parent, false);

        ImageView img = (ImageView) rowView.findViewById(R.id.imgHeader);
        TextView caption = (TextView) rowView.findViewById(R.id.txtCaption);
        ImageView imgAv = (ImageView) rowView.findViewById(R.id.imgAvailable);

        SoundPackage sp = packages.get(position);

        // check if a package is missing a header image
        if (sp.image != null) img.setImageBitmap(sp.image);

        caption.setText(sp.props.getProperty("caption","null"));

        // TODO set the OK image if the item is bought
        imgAv.setImageResource(R.drawable.available);

        return rowView;
    }
}