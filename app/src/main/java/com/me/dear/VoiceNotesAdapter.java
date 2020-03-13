package com.me.dear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VoiceNotesAdapter extends ArrayAdapter<VoiceNotesInfo> {
    private List<VoiceNotesInfo> items;
    private MediaPlayer mediaPlayer;
    private int length=0;

    VoiceNotesAdapter(Context context, ArrayList<VoiceNotesInfo> objects) {
        super(context, 0, objects);

        this.items = objects;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        VoiceNotesInfo item = items.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_item, parent, false);
        }

        TextView voiceName  = (TextView) convertView.findViewById(R.id.tvVRName);
        TextView duration  = (TextView) convertView.findViewById(R.id.tvDurationVNA);
        SeekBar seekBar = (SeekBar) convertView.findViewById(R.id.recordingSeekBarVNA);
        final ImageView  playBtn    = (ImageView) convertView.findViewById(R.id.ivPlayBtnVNA);
        final ImageView pauseBtn = (ImageView) convertView.findViewById(R.id.ivPauseBtnVNA);

        voiceName.setText(item.getName());

        PlayVoiceNotes playVoiceNotes = new PlayVoiceNotes(item.getPath(), duration, seekBar, playBtn, pauseBtn);
        playVoiceNotes.setUpMediaPlayer();
        mediaPlayer = playVoiceNotes.getMP();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer !=null) {
                    mediaPlayer.seekTo(length);
                    mediaPlayer.start();
                    playBtn.setVisibility(View.INVISIBLE);
                    pauseBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    mediaPlayer.pause();
                    length = mediaPlayer.getCurrentPosition();
                    playBtn.setVisibility(View.VISIBLE);
                    pauseBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        playVoiceNotes.moveSeekBar();

        return convertView;
    }
}
