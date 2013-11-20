package com.themagpi.activities;

import android.os.Bundle;
import android.os.Handler;

import com.themagpi.android.R;

public class MagPiSplashActivity extends SplashScreenActivity {
    Handler splashTimeout = new Handler();
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_magpi_splashscreen);
        this.setActivityTime(MagpiMainActivity.class, 3000);
    }

}
