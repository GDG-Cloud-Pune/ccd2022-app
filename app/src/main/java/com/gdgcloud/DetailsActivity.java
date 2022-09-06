package com.gdgcloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gdgcloud.preferences.UserPreferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class DetailsActivity extends AppCompatActivity {

    FloatingActionButton fabScanner;
    FirebaseAuth auth;
    UserPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        auth = FirebaseAuth.getInstance();
        preferences = new UserPreferences(this);
        fabScanner = findViewById(R.id.fabScanner);

        fabScanner.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        });
    }

    public void logOut(View v){
        preferences.logOut();
        auth.signOut();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();

    }
}