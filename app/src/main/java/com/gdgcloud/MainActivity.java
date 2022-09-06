package com.gdgcloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.gdgcloud.fragment.ScannerFragment;
import com.gdgcloud.preferences.UserPreferences;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    FrameLayout framelayout;

    private UserPreferences preferences;
    private HashMap<String, String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = new UserPreferences(this);
        userData = preferences.userData();

        framelayout = findViewById(R.id.framelayout);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(framelayout.getId(), new ScannerFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userData.get(UserPreferences.IS_EMAIL_ID) == null){
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }else {
            Log.d("TAG", "onStart: Login");
        }

    }
}