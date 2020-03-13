package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class VoiceNotesActivity extends AppCompatActivity {

    public static VoiceNotesActivity ins;

    private static ArrayList<VoiceNotesInfo> voiceNotesList;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseHelperAudio mDatabaseHelperAudio;
    private ArrayAdapter<VoiceNotesInfo> adapter;
    private ProgressDialog progressDialog;
    MediaPlayer mediaPlayer;

    FloatingActionButton goToRecord;
    boolean go=true;
    Toolbar toolbar;
    String vnName="", vnPath="";
    ArrayList<StorageReference> referencesVN;
    ArrayList<String> vnNames, vnPaths;
    ArrayList<Integer> vnDurations;
    int vnDuration = 0, noOfVn=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_notes);

        voiceNotesList = new ArrayList<>();
        vnNames = new ArrayList<>();
        vnPaths = new ArrayList<>();
        referencesVN = new ArrayList<>();
        vnDurations = new ArrayList<>();

        insUIItems();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        adapter = new VoiceNotesAdapter(this, voiceNotesList);
        ListView lv = (ListView) findViewById(R.id.lvVoiceNotes);

        lv.setAdapter(adapter);

        //this.deleteDatabase("audio");

        progressDialog = new ProgressDialog(this);
        mDatabaseHelperAudio = new DatabaseHelperAudio(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference myR = firebaseStorage.getReference(firebaseAuth.getUid() + "/audio");
        checkUserAuth();

        /**myR.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (final StorageReference reference : listResult.getItems()) {
                    noOfVn+=1;
                    String[] str = reference.toString().split("/");

                    vnName = str[str.length - 1];
                    referencesVN.add(reference);
                    vnNames.add(vnName);
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                populateData();
            }
        });**/

        populateData();

        goToRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go = false;
                startActivity(new Intent(getBaseContext(), RecordActivity.class));
            }
        });
    }

    public void populateData(){
        final Cursor cursor = mDatabaseHelperAudio.getData();
        //progressDialog.setMessage("Getting your data..");
        //progressDialog.show();

        if(cursor.getCount() == 0) //SHOW TEXT that you have 0 vn
            noOfVn = 0;

        else {
            while (cursor.moveToNext()) {
                if (cursor.getString(1).equals(firebaseAuth.getUid())) {
                    //voiceNotesList.set(i,new VoiceNotesInfo(cursor.getString(1), cursor.getString(2),
                    //cursor.getInt(3)));
                    //System.out.println(i + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getInt(3));
                    vnPath = cursor.getString(3);
                    vnDuration = cursor.getInt(4);
                    vnName = cursor.getString(2);
                    vnDurations.add(vnDuration);
                    vnPaths.add(vnPath);
                    voiceNotesList.add(new VoiceNotesInfo(vnName, vnPath, vnDuration));
                    noOfVn +=1;
                    adapter.notifyDataSetChanged();
                    Log.d("VNACT", "OK Hereeee");
                }
            }
        }
/**
        for (int i=0; i<vnNames.size(); i++){
            final int finalI = i;
            referencesVN.get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(uri.toString());
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                                progressDialog.dismiss();
                                vnDuration = mp.getDuration();
                                voiceNotesList.add(new VoiceNotesInfo(vnNames.get(finalI), vnPaths.get(finalI), vnDurations.get(finalI))); //FIX ADAPTER DURATION AND SEEK
                                adapter.notifyDataSetChanged();
                            }
                        });
                        mediaPlayer.prepare();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }**/

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.goToUserProfile:
                go=false;
                startActivity(new Intent(getBaseContext(), ProfileActivity.class));
                return true;
            case R.id.changeAct:
                go=false;
                startActivity(new Intent(getBaseContext(), RecordActivity.class));
                return true;

            case R.id.logOutBtn:
                go=false;
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

    @Override
    protected void onResume() {
        super.onResume();
        go = true;
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }
    }

    private void insUIItems(){
        goToRecord = (FloatingActionButton) findViewById(R.id.goToRecord);
        toolbar = (Toolbar) findViewById(R.id.voiceNotesToolbar);
    }
}
