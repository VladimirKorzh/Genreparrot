package com.genreparrot.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.genreparrot.adapters.AssetsHelper;
import com.genreparrot.adapters.SoundBatchPlayer;
import com.genreparrot.adapters.SoundPackage;
import com.genreparrot.adapters.StoreGridAdapter;
import com.genreparrot.app.MainActivity;
import com.genreparrot.app.R;

import java.util.ArrayList;

import util.IabHelper;
import util.IabResult;
import util.Purchase;

public class MediaFragment extends Fragment
{
    private StoreGridAdapter ad;
    static final String TAG = "MediaFragment";

    private IabHelper mHelper;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    public MediaFragment(){}


    public void getList(){
        ad.clear();
        for (SoundPackage sp : AssetsHelper.getInstance().packages_loaded.values()){
            ad.add(sp);
        }
        ad.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        getList();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_media, container, false);

        // initially provide an empty array of available packages
        ad = new StoreGridAdapter(getActivity(), new ArrayList<SoundPackage>());
        assert root != null;
        GridView grid = (GridView) root.findViewById(R.id.gridStore);

        grid.setAdapter(ad);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                purchasePackageDialog(i);
            }
        });


        mHelper = ((MainActivity) getActivity()).getIabHelper();

        return root;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }



    public void purchasePackageDialog(final int position){
        final ArrayAdapter<String> adList;
        final ArrayList<String> arr;

        final SoundPackage sp = ad.packages.get(position);

        // get only the aliases of files
        arr = new ArrayList<String>(sp.files.values());

        adList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, arr);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(sp.props.getProperty("caption"))
                .setSingleChoiceItems(adList, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SoundBatchPlayer.getInstance().playSingleFile(getActivity().getApplicationContext(),
                                sp.files.inverse().get(arr.get(i)));
                    }
                })
                .setCancelable(true);


        if (!sp.owned){

            alertDialog.setNegativeButton(R.string.btn_Back, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.setPositiveButton(R.string.btn_Purchase, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mHelper.launchPurchaseFlow(getActivity(), sp.SKU, RC_REQUEST,
                            mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                }
            });

        }
        else {

            alertDialog.setPositiveButton(R.string.btn_ThankYou, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

        }

        alertDialog.show();
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                Log.d(TAG, "Error purchasing. Authenticity verification failed.");
                return;
            }
            String purchased_package = purchase.getSku();
            AssetsHelper.getInstance().packages_loaded.get(purchased_package).owned = true;
            getList();
            Log.d(TAG, "Purchase successful.");
        }
    };


    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }



}