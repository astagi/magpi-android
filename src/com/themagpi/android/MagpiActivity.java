package com.themagpi.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.themagpi.api.Issue;


public class MagpiActivity extends SherlockFragmentActivity 
    implements HeadlinesFragment.OnHeadlineSelectedListener,
    CompatActionBarNavListener {
    
	HeadlinesFragment headFragment = new HeadlinesFragment();
    NewsFragment newsFragment = new NewsFragment();
    IssueFragment issueFragment = new IssueFragment();
    OnNavigationListener mOnNavigationListener;
	SherlockListFragment currentFragment;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_magpi, menu);
        return true;
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
        setContentView(R.layout.news_articles);

        mOnNavigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {
				return true;
			}
		};
        	
        CompatActionBarNavHandler handler = new CompatActionBarNavHandler(this);
        
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.dropdown_array,
                android.R.layout.simple_spinner_dropdown_item);
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
        
        getSupportActionBar().setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);
    	final String CATEGORIES[] = getResources().getStringArray(R.array.dropdown_array);
        for (int i = 0; i < CATEGORIES.length; i++) {
        	this.getSupportActionBar().addTab(this.getSupportActionBar().newTab().setText(
                CATEGORIES[i]).setTabListener(handler));
        }
        getSupportActionBar().setSelectedNavigationItem(0);
        
        if (this.isDualPane()) {

            if (savedInstanceState != null)
                return;

            issueFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.issue_fragment, issueFragment).commit();
        }
    }

    private boolean isDualPane() {
		return (findViewById(R.id.issue_fragment) != null);
	}

	public void onArticleSelected(Issue issue) {

        if (isDualPane()) {
            IssueFragment articleFrag = (IssueFragment)
                    getSupportFragmentManager().findFragmentById(R.id.issue_fragment);
            if(articleFrag != null)
            	articleFrag.updateIssueView(issue);
        } else {
        	Intent intent = new Intent(this, IssueActivity.class);
            intent.putExtra(IssueFragment.ARG_ISSUE, issue);
            startActivity(intent);
        }
    }

	@Override
	public void onCategorySelected(int catIndex) {
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        if(this.isDualPane())
        	fTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        else
        	fTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        int id;
        
        if(this.isDualPane())
        	id = R.id.headlines_fragment;
        else
        	id = R.id.fragment_container;
        
        if(currentFragment != null) {
        	fTransaction.remove(currentFragment);
			currentFragment.setMenuVisibility(false);
        }
        
		switch (catIndex) {
			case 0:
				currentFragment = headFragment;
		        if(issueFragment != null)
		        	issueFragment.setMenuVisibility(true);
	            fTransaction.replace(id, headFragment);
	            if(this.isDualPane())
	            	this.findViewById(R.id.issue_fragment).setVisibility(View.VISIBLE);
				break;
			case 1:
				currentFragment = newsFragment;
		        if(issueFragment != null)
		        	issueFragment.setMenuVisibility(false);
				fTransaction.replace(id, newsFragment);
				if(this.isDualPane())
					this.findViewById(R.id.issue_fragment).setVisibility(View.GONE);
				break;
			default:
				return;
		}
		
		fTransaction.commit();
	}

}
