package com.gdgcloud;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


import com.gdgcloud.preferences.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private EditText eEmailAddress, ePassword;
    private UserPreferences preferences;
    private HashMap<String, String> userData;

    private FirebaseAuth auth;
    
    ProgressDialog progress;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_sign_in);
        auth = FirebaseAuth.getInstance();
        preferences = new UserPreferences(this);
        userData = preferences.userData();
        progress = new ProgressDialog(this);
        eEmailAddress = findViewById(R.id.eEmailAddress);
        ePassword = findViewById(R.id.ePassword);

    }

    public void LogIn(View view) {
        progress.show();
        progress.setCancelable(false);
        progress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progress.setContentView(R.layout.progress_bar);
        if (eEmailAddress.getText().toString().isEmpty()){
            eEmailAddress.setError("Empty");
            progress.dismiss();
        }else if (!eEmailAddress.getText().toString().matches(emailPattern)){
            eEmailAddress.setError("Please check your mail-id");
            progress.dismiss();
        }else if (ePassword.getText().toString().isEmpty()){
            ePassword.setError("Empty");
            progress.dismiss();
        }else {
            auth.signInWithEmailAndPassword(
                    eEmailAddress.getText().toString(),
                    ePassword.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    preferences.SignIn(eEmailAddress.getText().toString(),ePassword.getText().toString());
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                    progress.dismiss();
                                }else {
                                    Toast.makeText(SignInActivity.this, "Please check your mail-id and password..!", Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "onFailure: "+e.getMessage());
                        progress.dismiss();
                            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userData.get(UserPreferences.IS_EMAIL_ID) != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            Log.d("TAG", "onStart: Logout");
        }
    }
}