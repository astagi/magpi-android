package com.themagpi.activities;

import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.google.android.gcm.GCMRegistrar;
import com.themagpi.adapters.PagerAdapter;
import com.themagpi.android.CompatActionBarNavHandler;
import com.themagpi.android.CompatActionBarNavListener;
import com.themagpi.android.Config;
import com.themagpi.android.R;
import com.themagpi.api.MagPiClient;
import com.themagpi.fragments.IssuesFragment;
import com.themagpi.fragments.NewsFragment;
import com.themagpi.interfaces.Refreshable;
import com.themagpi.interfaces.RefreshableContainer;

public class MagpiMainActivity extends SherlockFragmentActivity 
            implements ViewPager.OnPageChangeListener , CompatActionBarNavListener, RefreshableContainer {
    
    OnNavigationListener mOnNavigationListener;
    SherlockFragment currentFragment;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private Menu menu;
    private LayoutInflater inflater;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getSupportMenuInflater().inflate(R.menu.activity_magpi, menu);
        this.inflater = (LayoutInflater) ((SherlockFragmentActivity) this).getSupportActionBar().getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return true;
    }
    
    private void intialiseViewPager() {
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, IssuesFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, NewsFragment.class.getName()));
        this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        this.mViewPager = (ViewPager)super.findViewById(R.id.viewpager);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
    {
        switch(item.getItemId()) {
            case R.id.menu_refresh:
                refreshFragment((Refreshable)this.mPagerAdapter.getItem(mViewPager.getCurrentItem()));
                break;
            case R.id.menu_settings:
                Intent intent = new Intent(this, MagpiSettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void refreshFragment(Refreshable fragment) {
        if(fragment != null)
            fragment.refresh();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magpi_main);
        
        try {
	        GCMRegistrar.checkDevice(this);
	        GCMRegistrar.checkManifest(this);
	        final String idGcm = GCMRegistrar.getRegistrationId(this);
	        if (TextUtils.isEmpty(idGcm)) {
	        	Log.e("GCM", "NOT registered");
	            GCMRegistrar.register(MagpiMainActivity.this, Config.SENDER_ID);
	        } else {
	        	Log.e("GCM", "Already registered" + idGcm);
	        	if(isTimeToRegister())
	        		new MagPiClient().registerDevice(this, idGcm);
	        }
        } catch (UnsupportedOperationException ex) {
        	Log.e("GCM", "Google Cloud Messaging not supported - please install Google Apps package!");
        }

        mOnNavigationListener = new OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                return true;
            }
        };
        
        this.intialiseViewPager();
 
        CompatActionBarNavHandler handler = new CompatActionBarNavHandler(this);
        
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.dropdown_array,
                android.R.layout.simple_spinner_dropdown_item);
        
        getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
        getSupportActionBar().setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);
        
        final String CATEGORIES[] = getResources().getStringArray(R.array.dropdown_array);
        for (int i = 0; i < CATEGORIES.length; i++) {
            this.getSupportActionBar().addTab(this.getSupportActionBar().newTab().setText(
                CATEGORIES[i]).setTabListener(handler));
        }
        getSupportActionBar().setSelectedNavigationItem(0);
        
    }

    private boolean isTimeToRegister() {
    	SharedPreferences prefs = this.getSharedPreferences("MAGPI_REGISTRATION", Context.MODE_PRIVATE);
		long timeLastRegistration = prefs.getLong("TIME_LAST_REG", 0L);
		long currentTime = Calendar.getInstance().getTimeInMillis();
		Log.e("NOW", ""+ currentTime);
		Log.e("LAST",""+timeLastRegistration);
		if(currentTime > timeLastRegistration + 86400000L)
			return true;
		return false;
	}

	@Override
    public void onCategorySelected(int catIndex) {
        this.mViewPager.setCurrentItem(catIndex);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {        
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {        
    }

    @Override
    public void onPageSelected(int pos) {
        getSupportActionBar().setSelectedNavigationItem(pos);
    }

    @Override
    public void startRefreshIndicator() {
        if(menu != null)
            menu.findItem(R.id.menu_refresh).setActionView(inflater.inflate(R.layout.actionbar_refresh_progress, null));
        
    }

    @Override
    public void stopRefreshIndicator() {
        if(menu != null)
            menu.findItem(R.id.menu_refresh).setActionView(null);
        
    }

}
