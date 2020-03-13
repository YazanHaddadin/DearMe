package com.me.dear;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayVoiceNotes extends AppCompatActivity {

    private String path;
    private TextView dTv;
    private SeekBar sb;
    private ImageView playBtn, pauseBtn;
    boolean go = true;
    private MediaPlayer mediaPlayer;

    public PlayVoiceNotes(){

    }

    public PlayVoiceNotes(String path, TextView dTv, SeekBar sb, ImageView playBtn, ImageView pauseBtn){
        this.path = path;
        this.dTv = dTv;
        this.sb = sb;
        this.playBtn = playBtn;
        this.pauseBtn = pauseBtn;
    }

    public void setUpMediaPlayer(){
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            @SuppressLint("DefaultLocale") String time = String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()),
                    TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()))
            );

            dTv.setText(time);
            sb.setMax(mediaPlayer.getDuration());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public MediaPlayer getMP(){
        return mediaPlayer;
    }

    public void moveSeekBar(){
        final Handler mHandler = new Handler();
        PlayVoiceNotes.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(go) {
                    if (mediaPlayer != null) {
                        try {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition();
                            sb.setProgress(mCurrentPosition);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (mediaPlayer != null) {
                                if(progress / 10 == mediaPlayer.getDuration() / 10){
                                    playBtn.setVisibility(View.VISIBLE);
                                    pauseBtn.setVisibility(View.INVISIBLE);
                                    mediaPlayer.seekTo(0);
                                }

                                if (fromUser)
                                    mediaPlayer.seekTo(progress);

                                @SuppressLint("DefaultLocale") String time = String.format("%d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes(progress),
                                        TimeUnit.MILLISECONDS.toSeconds(progress) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress))
                                );
                                dTv.setText(time);
                            }
                        }
                    });
                    mHandler.postDelayed(this, 100);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        go = false;
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        go = false;
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        go =true;
    }
}
