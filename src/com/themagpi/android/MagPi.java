package com.themagpi.android;

import android.os.Bundle;
import android.os.Handler;

public class MagPi extends SplashScreenActivity {
    Handler splashTimeout = new Handler();
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.
        setContentView(R.layout.splashscreen);
        this.setActivityTime(MagpiActivity.class, 3000);
    }

}
