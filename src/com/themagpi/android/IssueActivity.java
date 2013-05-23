package com.themagpi.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class IssueActivity extends SherlockFragmentActivity implements RefreshableContainer {
    IssueFragment issueFragment = new IssueFragment();
    private Menu menu;
    private LayoutInflater inflater;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_articles);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        issueFragment.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, issueFragment);
        transaction.commit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getSupportMenuInflater().inflate(R.menu.issue, menu);
        this.inflater = (LayoutInflater) ((SherlockFragmentActivity) this).getSupportActionBar().getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
    {
        switch(item.getItemId()) {    
            case R.id.menu_refresh:
                ((Refreshable)issueFragment).refresh();
                break;
            case android.R.id.home:
                Intent i = new Intent();
                i.setClass(IssueActivity.this, MagpiActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
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
