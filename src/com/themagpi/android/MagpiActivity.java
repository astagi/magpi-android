package com.themagpi.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MagpiActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magpi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_magpi, menu);
        return true;
    }
    
}
