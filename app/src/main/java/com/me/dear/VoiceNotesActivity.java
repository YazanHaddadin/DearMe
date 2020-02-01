package com.me.dear;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class VoiceNotesActivity extends AppCompatActivity {

    ArrayList<VoiceNotesInfo> arL;
    private FirebaseAuth firebaseAuth;
    FloatingActionButton addNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_notes);

        arL = new ArrayList<>();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuth();

        populateList(arL);
        insUIItems();

        ArrayAdapter<VoiceNotesInfo> adapter = new VoiceNotesAdapter(this, arL);
        ListView lv = (ListView) findViewById(R.id.lvVoiceNotes);

        lv.setAdapter(adapter);

        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText taskEditText = new EditText(VoiceNotesActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(VoiceNotesActivity.this)
                        .setTitle("Name your voice note..")
                        .setMessage("Type 2 items such as: Yazan Haddadin")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                String list[] = task.split(" ");
                                arL.add(new VoiceNotesInfo(list[0], list[1], R.drawable.ic_launcher_foreground));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }
    }

    private void insUIItems(){
        addNewItem = (FloatingActionButton) findViewById(R.id.addNewBtn);
    }

    private void populateList(ArrayList<VoiceNotesInfo> ar){
        ar.add(new VoiceNotesInfo("Lion", "Haddadin", R.drawable.ic_launcher_foreground));
        ar.add(new VoiceNotesInfo("Tiger", "Drew", R.mipmap.ic_launcher));
        ar.add(new VoiceNotesInfo("Monkey", "Crowdey", R.mipmap.ic_launcher));
        ar.add(new VoiceNotesInfo("Elephant", "Fatty", R.drawable.ic_launcher_foreground));
        ar.add(new VoiceNotesInfo("Mountain", "Lion", R.mipmap.ic_launcher));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goToUserProfile:
                startActivity(new Intent(getBaseContext(), ProfileActivity.class));
                return true;
            case R.id.changeAct:
                startActivity(new Intent(getBaseContext(), RecordActivity.class));
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
}
