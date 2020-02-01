package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.UUID;

public class RecordActivity extends AppCompatActivity {

    Button recordBtn, recordingBtn, playBtn, stopBtn;

    public static String pathSave = "_dearme_recording.3gp";
    //String file = ContextCompat.getExternalFilesDirs(getApplicationContext(),null)[0].getAbsolutePath();

    final int REQUEST_PERMISSION_CODE = 1000;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuth();

        if(!checkPermissionFromDevice())
            requestPermission();

        //assignUI();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                        e.printStackTrace();
                    }

                    playBtn.setEnabled(false);
                    stopBtn.setEnabled(false);

                    Toast.makeText(RecordActivity.this, "RECORDING", Toast.LENGTH_SHORT).show();
                    recordBtn.setVisibility(View.INVISIBLE);
                    recordingBtn.setVisibility(View.VISIBLE);


                }else{
                    requestPermission();
                }
            }
            });

            recordingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.stop();

                    Toast.makeText(RecordActivity.this, "STOPPED", Toast.LENGTH_SHORT).show();
                    recordBtn.setVisibility(View.VISIBLE);
                    recordingBtn.setVisibility(View.INVISIBLE);
                    playBtn.setEnabled(true);
                    stopBtn.setEnabled(false);
                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopBtn.setEnabled(true);
                    recordBtn.setEnabled(false);
                    recordingBtn.setVisibility(View.INVISIBLE);

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                    }catch (IOException e){
                        Toast.makeText(RecordActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
                    }

                    mediaPlayer.start();
                }
            });

            stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopBtn.setEnabled(false);
                    recordBtn.setEnabled(true);
                    recordingBtn.setVisibility(View.INVISIBLE);

                    if(mediaPlayer!=null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setUpMediaRecorder();
                    }
                }
            });
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
            case android.R.id.home:
                finish();
                return true;

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
        recordingBtn = (Button) findViewById(R.id.recordingBtn);
        playBtn = (Button) findViewById(R.id.playBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
    }
}
