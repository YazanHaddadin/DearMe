package com.me.dear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ViewRecordingActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    String pathSave;

    EditText voiceNoteName;
    TextView cancelBtn, saveBtn, durationTxt;
    SeekBar seekBar;
    ImageView playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recording);

        assignUI();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.85), (int)(height*0.85));

        pathSave = getIntent().getStringExtra("savePath");

        setUpMediaPlayer();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });
    }

    private void setUpMediaPlayer(){
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(pathSave);
            mediaPlayer.prepare();
        }catch (IOException e){
            Toast.makeText(ViewRecordingActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("DefaultLocale") String time = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()))
        );

        durationTxt.setText(time);
    }

    private void assignUI() {
        voiceNoteName = (EditText) findViewById(R.id.etNameVoiceRecording);
        cancelBtn = (TextView) findViewById(R.id.tvCancelBtn);
        saveBtn = (TextView) findViewById(R.id.tvSaveVRBtn);
        seekBar = (SeekBar) findViewById(R.id.recordingSeekBar);
        playBtn = (ImageView) findViewById(R.id.ivPlayBtnVR);
        durationTxt = (TextView) findViewById(R.id.tvDuration);
    }
}
