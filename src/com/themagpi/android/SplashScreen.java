package com.themagpi.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
    Handler splashTimeout = new Handler();
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splashscreen);
    }
    
    protected void onResume() {
        super.onResume();
        splashTimeout.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MagpiActivity.class);
                startActivity(intent);
                SplashScreen.this.finish();
            }
            
        }, 5000);
    }
    
    public void onDestroy() {
        super.onDestroy();
        if(splashTimeout != null)
            splashTimeout.removeMessages(0);
    }

}
