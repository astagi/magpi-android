package com.themagpi.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.themagpi.android.Config;
import com.themagpi.android.R;

public class MagPiSplashActivity extends SplashScreenActivity {
    Handler splashTimeout = new Handler();
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_magpi_splashscreen);
        this.setActivityTime(MagpiMainActivity.class, 3000);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("download_folder", null) == null)
        	prefs.edit().putString("download_folder", Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Config.ISSUE_FOLDER).commit();
    }

}
