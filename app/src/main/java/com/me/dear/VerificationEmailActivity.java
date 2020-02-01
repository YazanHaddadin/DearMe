package com.me.dear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationEmailActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText userEmail;
    Button resendBtn, goToLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_email);

        assignUI();
        firebaseAuth = FirebaseAuth.getInstance();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.85), (int)(height*0.6));
    }

    public void reSendEmailVerification(View v){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        firebaseAuth.signOut();
                        Toast.makeText(getBaseContext(), "Verification email sent!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "Verification email failed to send.. try again in a few minutes", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void goToLogin(View v){
        finish();
        firebaseAuth.signOut();

        Intent lgn = new Intent(getBaseContext(), LoginActivity.class);

        if(!userEmail.getText().toString().isEmpty())
            lgn.putExtra("email", userEmail.getText().toString().trim());

        startActivity(lgn);
    }

    void assignUI(){
        userEmail = (EditText) findViewById(R.id.et_resetEmail);
        resendBtn = (Button) findViewById(R.id.resetEmailBtn);
        goToLoginBtn = (Button) findViewById(R.id.continueToLoginBtn);
    }
}
