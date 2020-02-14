package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

public class SaveVoiceNoteActivity extends AppCompatActivity {

    private static final String TAG = "SaveVoiceNoteActivity";

    MediaPlayer mediaPlayer;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseHelperAudio mDatabaseHelperAudio;

    String pathSave, name;
    int length=0, vnDur=0;
    boolean go=true;

    EditText voiceNoteName;
    TextView cancelBtn, saveBtn, durationTxt;
    SeekBar seekBar;
    ImageView playBtn, pauseBtn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_recording);

        assignUI();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.85), (int)(height*0.7));

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        mDatabaseHelperAudio = new DatabaseHelperAudio(this);

        pathSave = getIntent().getStringExtra("savePath");

        setUpMediaPlayer();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(length);
                mediaPlayer.start();
                playBtn.setVisibility(View.INVISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                length = mediaPlayer.getCurrentPosition();
                playBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.INVISIBLE);
            }
        });

        cancelBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        cancelBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                        break;

                    case MotionEvent.ACTION_UP:
                        AlertDialog dialog = new AlertDialog.Builder(SaveVoiceNoteActivity.this)
                                .setTitle("Delete your Recording")
                                .setMessage("Your recorded data will be lost forever.")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteRecording();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        cancelBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorRed, null));
                                    }
                                })
                                .create();
                        dialog.show();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        cancelBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorRed, null));
                }
                return true;
            }
        });

        saveBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        saveBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                        break;

                    case MotionEvent.ACTION_UP:
                        saveBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorRed, null));
                        uploadRecording();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        saveBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorRed, null));
                }
                return true;
            }
        });

        final Handler mHandler = new Handler();
        SaveVoiceNoteActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(go) {
                    if (mediaPlayer != null) {
                        try {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(mCurrentPosition);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
                                durationTxt.setText(time);
                            }
                        }
                    });
                    mHandler.postDelayed(this, 100);
                }
            }
        });
    }

    private void uploadRecording(){
        progressDialog.setMessage("Saving Recording..");
        progressDialog.show();
        name = voiceNoteName.getText().toString();

        StorageReference audioRef = storageReference.child(firebaseAuth.getUid()).child("audio").child(name);
        Uri audioPath = Uri.fromFile(new File(pathSave));
        UploadTask uploadTask = audioRef.putFile(audioPath);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("DID NOT WORK!!" + e.getMessage());
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("OKK!!");
                addData(firebaseAuth.getUid(), name, pathSave, vnDur);
                finish();
                startActivity(new Intent(getBaseContext(), VoiceNotesActivity.class));
                progressDialog.dismiss();
            }
        });
    }

    private void addData(String uid, String name, String path, int duration){
        boolean insertedData = mDatabaseHelperAudio.addData(uid, name, path, duration);

        if (insertedData)
            Log.d(TAG, "Successfully added into database");
        else
            Log.d(TAG, "Error accessing database");
    }

    private void deleteRecording() {
        go=false;
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        File file = new File(pathSave);
        boolean deleted = file.delete();

        if(deleted)
            System.out.println("OK");
        else
            System.out.println("NO GO");

        finish();

        cancelBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorRed, null));
    }

    private void setUpMediaPlayer(){
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(pathSave);
            mediaPlayer.prepare();
            @SuppressLint("DefaultLocale") String time = String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()),
                    TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()))
            );

            durationTxt.setText(time);
            vnDur = mediaPlayer.getDuration();
            seekBar.setMax(vnDur);
        }catch (IOException e){
            Toast.makeText(SaveVoiceNoteActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        go = true;
    }

    private void assignUI() {
        voiceNoteName = (EditText) findViewById(R.id.etNameVoiceRecording);
        cancelBtn = (TextView) findViewById(R.id.tvCancelBtn);
        saveBtn = (TextView) findViewById(R.id.tvSaveVRBtn);
        seekBar = (SeekBar) findViewById(R.id.recordingSeekBar);
        playBtn = (ImageView) findViewById(R.id.ivPlayBtnVR);
        durationTxt = (TextView) findViewById(R.id.tvDuration);
        pauseBtn = (ImageView) findViewById(R.id.ivPauseBtnVR);
    }
}
