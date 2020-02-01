package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText userName;
    EditText password;
    TextView goToRegister;
    Button loginBtn;
    TextView forgotPass;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        assignUIComp();

        final Intent registerPageActivity = new Intent(getBaseContext(), RegistrationActivity.class);
        final Intent voiceNotesPageAct = new Intent(getBaseContext(), VoiceNotesActivity.class);
        final Intent forgotPassAct = new Intent(getBaseContext(), ForgotPasswordActivity.class);
        final RelativeLayout loginPageLayout = (RelativeLayout) findViewById(R.id.loginPageLayout);
        loginPageLayout.setAlpha(1);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(voiceNotesPageAct);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(userName.getText().toString().trim(), password.getText().toString().trim());
            }
        });

        goToRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        goToRegister.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                        break;

                    case MotionEvent.ACTION_UP:
                        goToRegister.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                        finish();
                        startActivity(registerPageActivity);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        goToRegister.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                }
                return true;
            }
        });

        forgotPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        forgotPass.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                        break;

                    case MotionEvent.ACTION_UP:
                        forgotPass.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                        loginPageLayout.setAlpha(0.2f);
                        startActivity(forgotPassAct);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        loginPageLayout.setAlpha(1);
                        forgotPass.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        RelativeLayout lgn = (RelativeLayout) findViewById(R.id.loginPageLayout);
        lgn.setAlpha(1);
    }

    private boolean checkDetails(){
        boolean filledInDetails = false;

        String email    = userName.getText().toString();
        String pass    = password.getText().toString();

        if(email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please Fill in all your details!", Toast.LENGTH_SHORT).show();
        }else {
            filledInDetails = true;
        }

        return filledInDetails;
    }

    private void validate(String userName, String userPassword) {

        if(checkDetails()) {
            progressDialog.setMessage("It may take a moment to check your credentials!");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        checkEmailVerification();
                    } else {
                        FirebaseAuthException e = (FirebaseAuthException) task.getException();
                        Toast.makeText(getBaseContext(), "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        boolean isVerified = firebaseUser.isEmailVerified();
        RelativeLayout lgn = (RelativeLayout) findViewById(R.id.loginPageLayout);
        lgn.setAlpha(1);

        if(isVerified){
            finish();
            Toast.makeText(getBaseContext(), "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(), VoiceNotesActivity.class));
        }else{
            Toast.makeText(this, "Verify your email to Login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(), VerificationEmailActivity.class));
            lgn.setAlpha(0.2f);
        }
    }

    private void assignUIComp(){
        userName     = (EditText) findViewById(R.id.etUserNameL);
        password     = (EditText) findViewById(R.id.etUserPasswordL);
        goToRegister = (TextView) findViewById(R.id.tvToRegister);
        loginBtn     = (Button)   findViewById(R.id.btnLogin);
        forgotPass   = (TextView) findViewById(R.id.tv_forgotPassword);

        if(getIntent().getStringExtra("email") != null)
            userName.setText(getIntent().getStringExtra("email"));

    }
}
