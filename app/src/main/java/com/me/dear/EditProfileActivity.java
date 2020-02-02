package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private EditText newUserName, newUserDOB;
    ChipGroup chipGroup;
    Toolbar toolbar;
    private Button saveBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    String oldEmail, userDOB, userGender;
    Chip chip;
    Calendar c;
    DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        assignUI();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = firebaseDatabase.getReference("users/" + firebaseAuth.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userProfile = dataSnapshot.getValue(UserInformation.class);
                newUserName.setText(userProfile.getUserName());

                if(userProfile.getUserGender().equals("Male"))
                    chip = chipGroup.findViewById(R.id.chipMaleEP);
                else
                    chip = chipGroup.findViewById(R.id.chipFemaleEP);

                chip.setChecked(true);

                newUserDOB.setText(userProfile.getUserDOB());
                oldEmail = userProfile.getUserEmail();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                Chip chip = chipGroup.findViewById(i);
                if(chip != null){
                    userGender = chip.getHint().toString();
                }
            }
        });

        newUserDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                dpd = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dob = (month + 1) + "/" + dayOfMonth + "/" + year;
                        c.setTimeInMillis(0);
                        c.set(year, month, dayOfMonth, 0,0,0);
                        Date chosenDate = c.getTime();

                        DateFormat df_medium_us = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                        userDOB = df_medium_us.format(chosenDate);

                        newUserDOB.setText(userDOB);
                    }
                }, month, day, year);
                dpd.show() ;
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                c.set(1940, 0, 1);
                dpd.getDatePicker().setMinDate(c.getTimeInMillis());
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = oldEmail;
                String name = newUserName.getText().toString();
                String gender = userGender;
                String dob = newUserDOB.getText().toString();

                UserInformation userProfile = new UserInformation(email, name, gender, dob);
                myRef.setValue(userProfile);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void assignUI(){
        newUserName = (EditText) findViewById(R.id.etUserNameEP);
        newUserDOB = (EditText) findViewById(R.id.etUserDOBEP);
        saveBtn = (Button) findViewById(R.id.editSaveBtn);
        chipGroup = (ChipGroup) findViewById(R.id.chipUserGenderEP);
        toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
    }

}
