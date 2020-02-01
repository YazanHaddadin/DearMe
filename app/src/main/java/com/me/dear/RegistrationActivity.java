package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {


    private static final String TAG = "Registration_Activity";

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    EditText userName, password, confirmPassword, email, dateOfBirth;
    TextView goToLogin;
    Button   registerBtn;
    ChipGroup chipGroup;
    Calendar c;
    DatePickerDialog dpd;
    DatabaseHelper mDatabaseHelper;
    String userGender, userDOB;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        assignUIComp();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        mDatabaseHelper = new DatabaseHelper(this);
        progressDialog = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDetails()){
                progressDialog.setMessage("It may take a moment to create your profile!");
                progressDialog.show();
                    String user_email = email.getText().toString().trim();
                    String user_password = password.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendUserData();
                                sendEmailVerification(progressDialog);
                            }
                            else{
                                FirebaseException e = (FirebaseException) task.getException();
                                firebaseAuth.signOut();
                                Toast.makeText(RegistrationActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

        goToLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        goToLogin.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                        break;

                    case MotionEvent.ACTION_UP:
                        goToLogin.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                        finish();
                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        goToLogin.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                }
                return true;
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

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                dpd = new DatePickerDialog(RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dob = (month + 1) + "/" + dayOfMonth + "/" + year;
                        c.setTimeInMillis(0);
                        c.set(year, month, dayOfMonth, 0,0,0);
                        Date chosenDate = c.getTime();

                        DateFormat df_medium_us = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                        userDOB = df_medium_us.format(chosenDate);

                        dateOfBirth.setText(userDOB);
                    }
                }, month, day, year);
                dpd.show() ;
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                c.set(1940, 0, 1);
                dpd.getDatePicker().setMinDate(c.getTimeInMillis());
            }
        });
    }

    private boolean checkDetails(){
        boolean filledInDetails = false;

        String name    = userName.getText().toString();
        String pass    = password.getText().toString();
        String conpass = confirmPassword.getText().toString();
        String mail    = email.getText().toString();
        String gender  = userGender;
        String dob     = userDOB;

        if(name.isEmpty() || pass.isEmpty() || mail.isEmpty() || gender.isEmpty() || dob.isEmpty()){
            Toast.makeText(this,"Please Fill in all your details!", Toast.LENGTH_SHORT).show();
        }else if (!pass.equals(conpass)){
            Toast.makeText(this,"Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
        else {
            filledInDetails = true;
        }

        return filledInDetails;
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users/" + firebaseAuth.getUid());
        String e = email.getText().toString().trim();
        String un= userName.getText().toString();

        UserInformation userProfile = new UserInformation(e, un, userGender, userDOB);
        addData(firebaseAuth.getUid(), un, e, userGender, userDOB);
        myRef.setValue(userProfile);
    }

    private void sendEmailVerification(final ProgressDialog pd){
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final Intent lgn = new Intent(getBaseContext(), LoginActivity.class);

        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        pd.dismiss();
                        sendUserData();
                        firebaseAuth.signOut();
                        Toast.makeText(getBaseContext(), "Successfully Registered, Verification email sent!", Toast.LENGTH_SHORT).show();

                        if(!email.getText().toString().isEmpty())
                            lgn.putExtra("email", email.getText().toString().trim());

                        startActivity(lgn);

                    }else{
                        pd.dismiss();
                        Toast.makeText(getBaseContext(), "Verification email failed to send..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void addData(String uid, String username, String email, String gender, String dob){
        boolean insertedData = mDatabaseHelper.addData(uid, username, email, gender, dob);

        if (insertedData)
            Log.d(TAG, "Successfully added into database");
        else
            Log.d(TAG, "Error accessing database");
    }

    private void assignUIComp(){
        userName        = (EditText)  findViewById(R.id.etUserNameR);
        password        = (EditText)  findViewById(R.id.etUserPasswordR);
        confirmPassword = (EditText)  findViewById(R.id.etConfirmPassword);
        email           = (EditText)  findViewById(R.id.etUserEmailR);
        goToLogin       = (TextView)  findViewById(R.id.tvToLogin);
        registerBtn     = (Button)    findViewById(R.id.btnRegister);
        chipGroup       = (ChipGroup) findViewById(R.id.chipUserGender);
        dateOfBirth     = (EditText)  findViewById(R.id.etDOB);
    }
}
