package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.UUID;

public class RecordActivity extends AppCompatActivity {

    Button recordBtn;
    TextView stopBtn, pauseBtn, resumeBtn;
    Toolbar toolbar;

    String pathSave;

    final int REQUEST_PERMISSION_CODE = 1000;

    MediaRecorder mediaRecorder;
    private FirebaseAuth firebaseAuth;

    //FIX GOING BACK TO RECORD SCREEN, FIX THE BAR TO WORK WITH PAUSE AND CHANGE SECONDS

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuth();

        if(!checkPermissionFromDevice())
            requestPermission();

        assignUI();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final RelativeLayout recordPageLayout = (RelativeLayout) findViewById(R.id.recordPageLayout);
        recordPageLayout.setAlpha(1);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissionFromDevice()) {
                    pathSave = ContextCompat.getExternalFilesDirs(getApplicationContext(),
                            null)[0].getAbsolutePath() +
                            "/" + UUID.randomUUID().toString() +
                            "_dearme_recording.3gp";

                    setUpMediaRecorder();

                    try{
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    }catch (IOException e){
                        System.out.println(e.getMessage() + " WHY!!!!");
                    }

                    pauseBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                    stopBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                    recordBtn.setBackgroundResource(R.drawable.im_recording);
                    ColorStateList stateList = ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                    recordBtn.setBackgroundTintList(stateList);
                    recordBtn.setEnabled(false);

                    Toast.makeText(RecordActivity.this, "RECORDING", Toast.LENGTH_SHORT).show();

                }else{
                    requestPermission();
                }
            }
            });

        stopBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        stopBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                        break;

                    case MotionEvent.ACTION_UP:
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        recordPageLayout.setAlpha(0.8f);
                        recordBtn.setBackgroundResource(R.drawable.im_microphone);
                        recordBtn.setBackgroundTintList(null);
                        recordBtn.setEnabled(true);
                        stopBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                        pauseBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                        stopBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                        pauseBtn.setVisibility(View.VISIBLE);
                        resumeBtn.setVisibility(View.INVISIBLE);

                        Intent viewRcdAct = new Intent(getBaseContext(), SaveVoiceNoteActivity.class);
                        viewRcdAct.putExtra("savePath", pathSave);
                        startActivity(viewRcdAct);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        recordPageLayout.setAlpha(1);
                        stopBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                }
                return true;
            }
        });

        pauseBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        pauseBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                        break;

                    case MotionEvent.ACTION_UP:
                        mediaRecorder.pause();
                        Toast.makeText(RecordActivity.this, "PAUSED", Toast.LENGTH_SHORT).show();

                        pauseBtn.setVisibility(View.INVISIBLE);
                        pauseBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                        resumeBtn.setVisibility(View.VISIBLE);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        pauseBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                }
                return true;
            }
        });

        resumeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        resumeBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                        break;

                    case MotionEvent.ACTION_UP:
                        mediaRecorder.resume();
                        Toast.makeText(RecordActivity.this, "RESUMED", Toast.LENGTH_SHORT).show();

                        resumeBtn.setVisibility(View.INVISIBLE);
                        resumeBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                        pauseBtn.setVisibility(View.VISIBLE);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        resumeBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                }
                return true;
            }
        });
/**
            stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopBtn.setEnabled(false);
                    recordBtn.setEnabled(true);
                    stopRecordBtn.setVisibility(View.INVISIBLE);

                    if(mediaPlayer!=null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setUpMediaRecorder();
                    }
                }
            });**/
    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(RecordActivity.this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO},
                REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissionFromDevice(){
        int wr_external_storage_res = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudioRes = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return wr_external_storage_res == PackageManager.PERMISSION_GRANTED && recordAudioRes == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.goToUserProfile:
                startActivity(new Intent(getBaseContext(), ProfileActivity.class));
                return true;

            case R.id.changeAct:
                startActivity(new Intent(getBaseContext(), VoiceNotesActivity.class));
                return true;

            case R.id.logOutBtn:
                Toast.makeText(getBaseContext(), "Logging out..", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                finish();

                Intent backToLogin = new Intent(getBaseContext(), LoginActivity.class);
                backToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(backToLogin);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }

        RelativeLayout rcrd = (RelativeLayout) findViewById(R.id.recordPageLayout);
        rcrd.setAlpha(1);
    }

    private void checkUserAuth(){

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }

        boolean isVerified = firebaseAuth.getCurrentUser().isEmailVerified();

        if(!isVerified){
            finish();
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }
    }

    void assignUI(){
        recordBtn = (Button) findViewById(R.id.recordBtn);
        pauseBtn = (TextView) findViewById(R.id.tvPauseBtn);
        stopBtn = (TextView) findViewById(R.id.tvStopBtn);
        resumeBtn = (TextView) findViewById(R.id.tvResumeBtn);
        toolbar = (Toolbar) findViewById(R.id.recordToolbar);
    }
}
