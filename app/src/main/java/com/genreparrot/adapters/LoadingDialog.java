package com.genreparrot.adapters;

import android.app.ProgressDialog;
import android.content.Context;

import com.genreparrot.app.R;

public class LoadingDialog {
    public static ProgressDialog loading;
    private static LoadingDialog instance = null;
    private Context context;
    public LoadingDialog(Context c) {
        loading = new ProgressDialog(c);
        loading.setTitle(c.getString(R.string.loadingDialogTitle));
        loading.setCancelable(false);
        loading.setInverseBackgroundForced(false);
        context = c;
    }

    public LoadingDialog getInstance(){
        return instance;
    }


    public Runnable changeMsg(final String msg){
        return new Runnable() {
            @Override
            public void run() {
                loading.setMessage(msg);
                loading.show();
            }
        };
    }
}
