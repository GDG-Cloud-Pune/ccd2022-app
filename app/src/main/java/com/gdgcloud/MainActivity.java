package com.gdgcloud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.gdgcloud.fragment.ScannerFragment;

public class MainActivity extends AppCompatActivity {
    FrameLayout framelayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        framelayout = findViewById(R.id.framelayout);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(framelayout.getId(), new ScannerFragment())
                .commit();
    }
}