package com.themagpi.android;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.themagpi.api.Issue;

import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.*;

public class MagpiActivity extends SherlockFragmentActivity 
    implements HeadlinesFragment.OnHeadlineSelectedListener {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_magpi, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
	{
	    int id = item.getItemId();
	    if (id == android.R.id.home) {

	        return true;

	    } else {
	        return super.onOptionsItemSelected(item);
	    }
	}

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_articles);
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            HeadlinesFragment firstFragment = new HeadlinesFragment();

            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    public void onArticleSelected(Issue issue) {

        IssueFragment articleFrag = (IssueFragment)
                getSupportFragmentManager().findFragmentById(R.id.issue_fragment);

        if (articleFrag != null) {
            articleFrag.updateIssueView(issue);
        } else {
            IssueFragment newFragment = new IssueFragment();
            Bundle args = new Bundle();
            args.putParcelable(IssueFragment.ARG_ISSUE, issue);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    }
}
