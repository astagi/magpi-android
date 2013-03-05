package com.themagpi.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;

public class HeadlinesFragment extends SherlockFragment implements Refreshable {
    MagPiClient client = new MagPiClient();
    int layout;
    private Menu menu;
    private LayoutInflater inflater;
    private GridView mGridView;
    private IssueGridAdapter mGridAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.issues_grid, container, false);
        
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        
        return view;
    }
    
    public void updateGrid(ArrayList<Issue> issues) {
        Activity activity = getActivity();

        if (activity != null) {
            mGridAdapter = new IssueGridAdapter(activity, issues);
            
            if (mGridView != null) {
                mGridView.setAdapter(mGridAdapter);
            }
            
            mGridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onGridItemClick((GridView) parent, view, position, id);
                }
                
            });
        }
    }
    
    public void onGridItemClick(GridView g, View v, int position, long id) {
        Activity activity = getActivity();
        
        if (activity != null) {            
            Intent intent = new Intent(this.getSherlockActivity(), IssueActivity.class);
            intent.putExtra(IssueFragment.ARG_ISSUE,(Issue) mGridAdapter.getItem(position));
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.refresh();
    }


    @Override
    public void refresh() {
        client.getIssues(new MagPiClient.OnIssuesReceivedListener() {
            public void onReceived(ArrayList<Issue> issues) {         
    			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HeadlinesFragment.this.getSherlockActivity());
                prefs.edit().putString("last_issue", issues.get(0).getId()).commit();
                updateGrid(issues); 
                try {
                    if(menu != null)
                        menu.findItem(R.id.menu_refresh).setActionView(null);
                } catch (NullPointerException ex) {}
            }
            public void onError(int error) {
                if(menu != null)
                    menu.findItem(R.id.menu_refresh).setActionView(null);
            }
            
        });
        
        if(menu != null)
            menu.findItem(R.id.menu_refresh).setActionView(inflater.inflate(R.layout.actionbar_refresh_progress, null));
        
    }
}