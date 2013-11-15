package com.themagpi.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;
import com.themagpi.activities.IssueDetailsActivity;
import com.themagpi.adapters.IssuesGridAdapter;
import com.themagpi.android.R;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;
import com.themagpi.interfaces.Refreshable;
import com.themagpi.interfaces.RefreshableContainer;

public class IssuesFragment extends SherlockFragment implements Refreshable {
    MagPiClient client = new MagPiClient();
    int layout;
    private GridView mGridView;
    private IssuesGridAdapter mGridAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issues_grid, container, false);
        return view;
    }
    
    public void updateGrid(ArrayList<Issue> issues) {
        Activity activity = getActivity();

        if (activity != null) {
            mGridAdapter = new IssuesGridAdapter(activity, issues);
            mGridView = (GridView) getActivity().findViewById(R.id.issues_grid_view);
            mGridView.setAdapter(mGridAdapter);
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
            Intent intent = new Intent(this.getSherlockActivity(), IssueDetailsActivity.class);
            intent.putExtra(IssueDetailsFragment.ARG_ISSUE,(Issue) mGridAdapter.getItem(position));
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ONRES", "ONRED");
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.refresh();
    }

    @Override
    public void refresh() {
        ((RefreshableContainer) getActivity()).startRefreshIndicator();
        client.getIssues(new MagPiClient.OnIssuesReceivedListener() {
            public void onReceived(ArrayList<Issue> issues) {         
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(IssuesFragment.this.getSherlockActivity());
                prefs.edit().putString("last_issue", issues.get(issues.size()-1).getId()).commit();
                updateGrid(issues); 
                ((RefreshableContainer) getActivity()).stopRefreshIndicator();
                getActivity().findViewById(R.id.progress_issues).setVisibility(View.GONE);
            }
            public void onError(int error) {
                ((RefreshableContainer) getActivity()).stopRefreshIndicator();
                Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_LONG).show();
            }
            
        });      
    }
    
    public void onPause() {
    	super.onPause();
    	Log.e("PAUSE", "PAUSE");
    	client.close(getActivity());
    	((RefreshableContainer) getActivity()).stopRefreshIndicator();
    }
}