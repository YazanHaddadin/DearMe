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
import com.google.firebase.auth.FirebaseAuthException;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText userEmailReset;
    Button forgotPassBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        assignUIComp();

        firebaseAuth = FirebaseAuth.getInstance();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.85), (int)(height*0.45));

        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = userEmailReset.getText().toString().trim();

                if(user_email.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                }else {
                    firebaseAuth.sendPasswordResetEmail(user_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "Password Reset email has been sent.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            }else{
                                FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void assignUIComp(){
        userEmailReset = (EditText) findViewById(R.id.et_resetEmail);
        forgotPassBtn = (Button) findViewById(R.id.resetBtn);
    }
}
