package com.themagpi.android;

import java.util.List;
import java.util.Vector;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MagpiActivity extends SherlockFragmentActivity implements ViewPager.OnPageChangeListener , CompatActionBarNavListener{
    
    HeadlinesFragment headFragment = new HeadlinesFragment();
    NewsFragment newsFragment = new NewsFragment();
    IssueFragment issueFragment = new IssueFragment();
    OnNavigationListener mOnNavigationListener;
    SherlockFragment currentFragment;
	private PagerAdapter mPagerAdapter;
	private ViewPager mViewPager;
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_magpi, menu);
        return true;
    }
    
    private void intialiseViewPager() {

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, HeadlinesFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, NewsFragment.class.getName()));
		this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
		//
		this.mViewPager = (ViewPager)super.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
    {
        switch(item.getItemId()) {
            case R.id.menu_refresh:
                refreshFragment((Refreshable)currentFragment);
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
        setContentView(R.layout.activity_magpi);
        
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(MagpiActivity.this, Config.SENDER_ID);
        } else {
            RequestParams params = new RequestParams();
            params.put("id", regId);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(Config.SERVICE_URL + "/register", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONArray timeline) {

                }
            });
            Log.e("ERRORREGISTERING", "Already registered");
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

    @Override
    public void onCategorySelected(int catIndex) {
		this.mViewPager.setCurrentItem(catIndex);
    }

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int pos) {
		getSupportActionBar().setSelectedNavigationItem(pos);
	}

}
