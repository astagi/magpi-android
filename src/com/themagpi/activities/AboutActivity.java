package com.themagpi.activities;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.themagpi.android.R;

public class AboutActivity extends SherlockActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			((TextView)this.findViewById(R.id.label_version)).setText(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
    {
        switch(item.getItemId()) {    
            case android.R.id.home:
                Intent i = new Intent();
                i.setClass(AboutActivity.this, MagpiMainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
