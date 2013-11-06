package com.themagpi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {
    private Handler splashTimeout = new Handler();
    private Class<?> cls;
    private long time;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    public void setActivityTime(Class<?> cls, long time) {
        this.cls = cls;
        this.time = time;
    }

    @Override
    protected void onResume() {
        super.onResume();
        splashTimeout.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, cls);
                startActivity(intent);
                SplashScreenActivity.this.finish();
            }            
        }, time);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(splashTimeout != null)
            splashTimeout.removeMessages(0);
    }
}
