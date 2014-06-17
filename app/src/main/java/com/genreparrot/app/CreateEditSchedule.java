package com.genreparrot.app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.genreparrot.adapters.AssetsHelper;
import com.genreparrot.adapters.SoundBatchPlayer;
import com.genreparrot.adapters.SoundPackage;
import com.genreparrot.database.Schedule;
import com.genreparrot.database.ScheduleDAO;
import com.genreparrot.fragments.CreateEditFragment;

import java.util.ArrayList;


public class CreateEditSchedule extends ActionBarActivity {

    private LinearLayout extraSettings;

    public ArrayList<String> GetListOfTrainingFiles(){
        ArrayList<String> arr = new ArrayList<String>();

        for (SoundPackage sp : AssetsHelper.getInstance().packages_loaded.values()){
            if (sp.owned){
                arr.addAll(sp.files.values());
            }
        }

        return arr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_alarm);
        CreateEditFragment placeholderFragment = new CreateEditFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, placeholderFragment)
                    .commit();
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onResume()
    {
        initVolumeControls();
        extraSettings = (LinearLayout) findViewById(R.id.layoutExtraSettings);
        super.onResume();
    }

    private void initVolumeControls()
    {
        try
        {
            SeekBar volumeSeekbar;
            AudioManager audioManager;
            volumeSeekbar = (SeekBar) findViewById(R.id.seekVolumeMusic);
            audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            final AudioManager finalAudioManager = audioManager;
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {

                public void onStopTrackingTouch(SeekBar arg0)
                {
                }


                public void onStartTrackingTouch(SeekBar arg0)
                {
                }


                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    finalAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void btnRepsPerSessionClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.RepsPerSessionDialog)
                .setItems(R.array.RepsPerSession, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TextView t = (TextView) findViewById(R.id.txtRepsPerSession);
                        t.setText(getResources().getStringArray(R.array.RepsPerSession)[which]);
                    }
                });
        builder.show();
    }
    public void btnRepsIntervalClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.RepsIntervalDialog)
                .setItems(R.array.RepsInterval, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TextView t = (TextView) findViewById(R.id.txtRepsInterval);
                        t.setText(getResources().getStringArray(R.array.RepsInterval)[which]);
                    }
                });
        builder.show();
    }
    public void btnSessionIntervalClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.SessionIntervalDialog)
                .setItems(R.array.SessionInterval, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TextView t = (TextView) findViewById(R.id.txtSessionInterval);
                        t.setText(getResources().getStringArray(R.array.SessionInterval)[which]);
                    }
                });
        builder.show();
    }
    public void btnAttractorSoundClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.AttractorSoundDialog)
                .setItems(R.array.AttractorSound, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TextView t = (TextView) findViewById(R.id.txtAttractorSound);
                        t.setText(getResources().getStringArray(R.array.AttractorSound)[which]);
                    }
                });
        builder.show();
    }
    public void btnTimeClick(View view){
        TextView t = null;
        switch( view.getId() ){
            case R.id.layoutStartTimeBlock:
                t = (TextView) findViewById(R.id.txtStartTime);
                break;
            case R.id.layoutEndTimeBlock:
                t = (TextView) findViewById(R.id.txtEndTime);
                break;
        }

        final TextView finalT2 = t;
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String str = String.format("%d:%02d", hour, minute);
                finalT2.setText(str);
            }
        };

        if (t.getText().equals(getString(R.string.lblNotSelected))){
            Time today = new Time();
            today.setToNow();
            final TextView finalT1 = t;
            new TimePickerDialog(this, listener, today.hour,today.minute,true).show();
        }
        else{
            String[] parts = ((String)t.getText()).split(":");
            int hours = Integer.valueOf(parts[0]);
            int minutes = Integer.valueOf(parts[1]);
            final TextView finalT = t;
            new TimePickerDialog(this, listener, hours,minutes,true).show();
        }
    }
    public void btnPlayOnce(View view){
        TextView t = (TextView) findViewById(R.id.txtTrainingSound);
        String filepath = AssetsHelper.getInstance().getFilepathFromFileAlias((String) t.getText());
        SoundBatchPlayer.getInstance().playSingleFile(getBaseContext(), filepath);
    }
    public void btnExtraSettingsClick(View view){
        Button b = (Button) findViewById(R.id.btnExtraSettings);
        if (extraSettings.getVisibility()==View.GONE){

            b.setText(getString(R.string.btn_hide_extra_settings));
            //set Visible
            extraSettings.setVisibility(View.VISIBLE);

            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            extraSettings.measure(widthSpec, heightSpec);

            ValueAnimator mAnimator = slideAnimator(0, extraSettings.getMeasuredHeight());
            mAnimator.start();
        }
        else {
            b.setText(getString(R.string.btn_display_extra_settings));
            int finalHeight = extraSettings.getHeight();

            ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    //Height=0, but it set visibility to GONE
                    extraSettings.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            mAnimator.start();
        }

    }
    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = extraSettings.getLayoutParams();
                layoutParams.height = value;
                LinearLayout item = (LinearLayout) findViewById(R.id.linearLayout3);
                ScrollView scroller = (ScrollView) findViewById(R.id.scrollView);
                scroller.smoothScrollTo(0, item.getBottom());
                extraSettings.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }




    public void btnTrainingSoundClick(View view){
        final ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, GetListOfTrainingFiles());

        new AlertDialog.Builder(this)
                .setTitle(R.string.SelectTrainingSoundDialogTitle)
                .setSingleChoiceItems(ad, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView t = (TextView) findViewById(R.id.txtTrainingSound);
                        t.setText(ad.getItem(i));
                        String filepath = AssetsHelper.getInstance().getFilepathFromFileAlias(ad.getItem(i));
                        SoundBatchPlayer.getInstance().playSingleFile(getBaseContext(), filepath);
                    }
                })
                .setNegativeButton(R.string.btnRecording, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                        startActivityForResult(intent, 0);
                    }
                })
                .setPositiveButton(R.string.btnSelect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_edit_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_cancel_schedule) {
            this.finish();
        }
        if (id == R.id.action_save_schedule) {
            TextView filename = (TextView) findViewById(R.id.txtTrainingSound);
            SeekBar volume = (SeekBar) findViewById(R.id.seekVolumeMusic);
            TextView starttime = (TextView) findViewById(R.id.txtStartTime);
            TextView endtime = (TextView) findViewById(R.id.txtEndTime);
            TextView repspersession = (TextView) findViewById(R.id.txtRepsPerSession);
            TextView repsinterval = (TextView) findViewById(R.id.txtRepsInterval);
            TextView sessioninterval = (TextView) findViewById(R.id.txtSessionInterval);
            TextView attractortimes = (TextView) findViewById(R.id.txtAttractorSound);

            if ( filename.getText().toString().equals(getString(R.string.lblNotSelected) ) ) {
                Toast.makeText(this, getString(R.string.ScheduleErrorMsgFile), Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
            if (starttime.getText().toString().equals(getString(R.string.lblNotSelected)) || endtime.getText().toString().equals(getString(R.string.lblNotSelected))) {
                Toast.makeText(this, getString(R.string.ScheduleErrorMsgTime), Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }

            Time timeStart = new Time();
            timeStart.set(Schedule.timeStringToMillis(starttime.getText().toString()));

            Time timeEnd = new Time();
            timeEnd.set(Schedule.timeStringToMillis(endtime.getText().toString()));

            if (timeStart.after(timeEnd)){
                Toast.makeText(this, getString(R.string.ScheduleErrorMsgTimeWrong), Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
            ScheduleDAO SchDao = new ScheduleDAO(getBaseContext());
            SchDao.open();

            Bundle b = getIntent().getExtras();
            int scheduleID = b.getInt("scheduleID");
            String filepath = AssetsHelper.getInstance().getFilepathFromFileAlias((String) filename.getText());
            if (scheduleID != -1) {
                // updating old
                boolean sch = SchDao.updateSchedule(scheduleID,
                        filepath,
                        volume.getProgress(),

                        Schedule.timeStringToMillis(starttime.getText().toString()),
                        Schedule.timeStringToMillis(endtime.getText().toString()),

                        Integer.parseInt(repspersession.getText().toString()),
                        Integer.parseInt(repsinterval.getText().toString()),
                        Integer.parseInt(sessioninterval.getText().toString()),
                        Integer.parseInt(attractortimes.getText().toString()),
                        0
                );
                if (sch){
                    Toast.makeText(this, getString(R.string.ScheduleUpdatedMsg), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, getString(R.string.ScheduleUpdatedErrorMsg), Toast.LENGTH_LONG).show();
                }

            }
            else {
                // creating new
                Schedule sch = SchDao.createSchedule(
                        filepath,
                        volume.getProgress(),

                        Schedule.timeStringToMillis(starttime.getText().toString()),
                        Schedule.timeStringToMillis(endtime.getText().toString()),

                        Integer.parseInt(repspersession.getText().toString()),
                        Integer.parseInt(repsinterval.getText().toString()),
                        Integer.parseInt(sessioninterval.getText().toString()),
                        Integer.parseInt(attractortimes.getText().toString()),
                        0
                );
                Toast.makeText(this, getString(R.string.ScheduleCreatedMsg), Toast.LENGTH_LONG).show();
            }
            SchDao.close();
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    TextView t = (TextView) findViewById(R.id.txtTrainingSound);
                    t.setText(data.getDataString());

                    Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(data.getDataString()));

                    Log.d("debug", "User selected recording: "+data.getDataString()+" "+ r.getTitle(this));
                    SoundBatchPlayer.getInstance().playSingleFile(getBaseContext(), data.getDataString());
                    break;
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (isFinishing() == true){
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }
    }
}
