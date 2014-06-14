package com.genreparrot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.genreparrot.app.R;

import java.util.HashMap;
import java.util.Iterator;

public class InfoFragment extends Fragment {
    public InfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HashMap<Integer,String> items = new HashMap<Integer, String>();
        items.put(R.id.layoutAbout,getString(R.string.textAbout));
        items.put(R.id.layoutUsage,getString(R.string.textAbout));
        items.put(R.id.layoutNewSounds,getString(R.string.textAbout));
        items.put(R.id.layoutShowcase,getString(R.string.textAbout));
        items.put(R.id.layoutChangelog,getString(R.string.textAbout));

        View vg = inflater.inflate(R.layout.fragment_info, container, false);

        LinearLayout l = null;
        TextView t = null;
        Iterator<Integer> keySetIterator = items.keySet().iterator();
        while(keySetIterator.hasNext()){
            Integer key = keySetIterator.next();
            l = (LinearLayout) vg.findViewById(key);
            t = (TextView) l.findViewById(R.id.txtBody);
            t.setText(items.get(key));
            l.setOnClickListener(expand_listener);
        }
        return vg;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View.OnClickListener expand_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView txtBody = (TextView) view.findViewById(R.id.txtBody);
            if ( txtBody.getVisibility() == View.GONE ){
                // set visible
                txtBody.setVisibility(View.VISIBLE);
            }
            else {
                // set invisible
                txtBody.setVisibility(View.GONE);
            }

        }
    };
}


//    public void btnExtraSettingsClick(View view){
//        Button b = (Button) view.findViewById(R.id.);
//        if (extraSettings.getVisibility()==View.GONE){
//
//            b.setText(getString(R.string.btn_hide_extra_settings));
//            //set Visible
//            extraSettings.setVisibility(View.VISIBLE);
//
//            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//            final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//            extraSettings.measure(widthSpec, heightSpec);
//
//            ValueAnimator mAnimator = slideAnimator(0, extraSettings.getMeasuredHeight());
//            mAnimator.start();
//        }
//        else {
//            b.setText(getString(R.string.btn_display_extra_settings));
//            int finalHeight = extraSettings.getHeight();
//
//            ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
//
//            mAnimator.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animator) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animator) {
//                    //Height=0, but it set visibility to GONE
//                    extraSettings.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animator) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animator) {
//
//                }
//            });
//            mAnimator.start();
//        }
//
//    }
//
//    private ValueAnimator slideAnimator(int start, int end) {
//
//        ValueAnimator animator = ValueAnimator.ofInt(start, end);
//
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                //Update Height
//                int value = (Integer) valueAnimator.getAnimatedValue();
//                ViewGroup.LayoutParams layoutParams = extraSettings.getLayoutParams();
//                layoutParams.height = value;
//                LinearLayout item = (LinearLayout) findViewById(R.id.linearLayout3);
//                ScrollView scroller = (ScrollView) findViewById(R.id.scrollView);
//                scroller.smoothScrollTo(0, item.getBottom());
//                extraSettings.setLayoutParams(layoutParams);
//            }
//        });
//        return animator;
//    }
