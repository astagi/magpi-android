package com.themagpi.android;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MagpiSettingsActivity extends PreferenceActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
