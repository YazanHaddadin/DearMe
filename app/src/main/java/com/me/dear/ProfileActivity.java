package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "Profile_Activity";

    ArrayList<UserProfileInfo> userArrayList = new ArrayList<>();
    ArrayAdapter<UserProfileInfo> adapter;

    private FirebaseAuth firebaseAuth;
    DatabaseHelper mDatabaseHelper;

    ProgressDialog progressDialog;
    ImageView userProfile;
    Button deleteAccountBtn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuth();

        assignUIElements();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressDialog = new ProgressDialog(this);
        adapter = new UserProfileAdapter(this, userArrayList);
        ListView lv = (ListView) findViewById(R.id.lvUserProfile);
        lv.setAdapter(adapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users/" + firebaseAuth.getUid());

        mDatabaseHelper = new DatabaseHelper(this);

        nullifyList(userArrayList);
        populateData();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInformation userProfile = dataSnapshot.getValue(UserInformation.class);
                try {
                    final String username = userProfile.getUserName();
                    final String userEmail = userProfile.getUserEmail();
                    final String userGender = userProfile.getUserGender();
                    final String userDOB = userProfile.getUserDOB();

                    userArrayList.set(0, new UserProfileInfo("Username", username));
                    userArrayList.set(1, new UserProfileInfo("Email", userEmail));
                    userArrayList.set(2, new UserProfileInfo("Gender", userGender));
                    userArrayList.set(3, new UserProfileInfo("Date of Birth", userDOB));

                    adapter.notifyDataSetChanged();

                    addData(firebaseAuth.getUid(), username, userEmail, userGender, userDOB);
                }catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage() + databaseError.getCode());
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Delete your Account")
                        .setMessage("Are you sure you want to delete all of your data?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAccount();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        MenuItem editBtn = menu.findItem(R.id.editProfile);
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                editBtn.setVisible(false);
                return false; // Not connected to an access point
            }
            editBtn.setVisible(true);
            return true; // Connected to an access point
        }
        else {
            editBtn.setVisible(false);
            return false; // Wi-Fi adapter is OFF
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                return true;
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addData(String uid, String username, String email, String gender, String dob){
        boolean insertedData = mDatabaseHelper.addData(uid, username, email, gender, dob);

        if (insertedData)
            Log.d(TAG, "Successfully added into database");
        else
            Log.d(TAG, "Error accessing database");
    }

    public void populateData(){
        Cursor cursor = mDatabaseHelper.getData();

        if(cursor.getCount() == 0)
            nullifyList(userArrayList);

        else{
            while (cursor.moveToNext()) {
                if(cursor.getString(0).equals(firebaseAuth.getUid())) {
                    userArrayList.set(0,new UserProfileInfo("Username", cursor.getString(1)));
                    userArrayList.set(1,new UserProfileInfo("Email", cursor.getString(2)));
                    userArrayList.set(2,new UserProfileInfo("Gender", cursor.getString(3)));
                    userArrayList.set(3,new UserProfileInfo("Date of Birth", cursor.getString(4)));
                }
                else
                    nullifyList(userArrayList);
            }
        }

        cursor.close();
    }

    public void deleteAccount(){
        firebaseAuth = FirebaseAuth.getInstance();
        getBaseContext().deleteDatabase(mDatabaseHelper.getDatabaseName());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users");
        myRef.child(firebaseAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().delete();
                    firebaseAuth.signOut();

                    Intent backToLogin = new Intent(ProfileActivity.this, LoginActivity.class);
                    backToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(backToLogin);

                    toastMessage("Account successfully deleted, sorry to see you go!");
                }else
                    toastMessage("An error has occurred, try again.");
            }
        });
    }

    private void nullifyList(ArrayList<UserProfileInfo> aL){
        if(aL.isEmpty()) {
            aL.add(new UserProfileInfo("Username", ""));
            aL.add(new UserProfileInfo("Email", ""));
            aL.add(new UserProfileInfo("Gender", ""));
            aL.add(new UserProfileInfo("Date of Birth", ""));
        }else{
            aL.set(0,new UserProfileInfo("Username", ""));
            aL.set(1,new UserProfileInfo("Email", ""));
            aL.set(2,new UserProfileInfo("Gender", ""));
            aL.set(3,new UserProfileInfo("Date of Birth", ""));
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

    private void assignUIElements(){
        deleteAccountBtn = (Button) findViewById(R.id.deleteAccountBtn);
        userProfile = (ImageView) findViewById(R.id.ivUserProfile);
        toolbar = (Toolbar) findViewById(R.id.userProfileToolbar);
    }

    private void toastMessage(String mssg){
        Toast.makeText(getBaseContext(), mssg, Toast.LENGTH_SHORT).show();
    }
}
