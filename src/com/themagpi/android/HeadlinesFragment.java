package com.themagpi.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;

public class HeadlinesFragment extends SherlockListFragment implements Refreshable {
    OnHeadlineSelectedListener mCallback;
    ArrayList<Issue> issues = new ArrayList<Issue>();
    MagPiClient client = new MagPiClient();
    int layout;
	private Menu menu;
	private LayoutInflater inflater;

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(Issue issue);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
        this.setHasOptionsMenu(true);
        this.refresh();
    }
    

    @Override
    public void onStart() {
        super.onStart();

        if (getFragmentManager().findFragmentById(R.id.issue_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        
        ((SherlockFragmentActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    	this.menu = menu;
        this.inflater = (LayoutInflater) ((SherlockFragmentActivity) getActivity()).getSupportActionBar().getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void onPause() {
        super.onPause();
        if(getActivity() != null && client != null)
            client.close(getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(mCallback != null)
    		mCallback.onArticleSelected(issues.get(position));
        getListView().setItemChecked(position, true);
    }


	@Override
	public void refresh() {
        client.getIssues(new MagPiClient.OnIssuesReceivedListener() {
            public void onReceived(ArrayList<Issue> issues) {         
                HeadlinesFragment.this.issues = issues;
                try {
                    setListAdapter(createIssueAdapter(issues)); 
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
	
	public SimpleAdapter createIssueAdapter(ArrayList<Issue> issues) {
		ArrayList<HashMap<String,Object>> list_populate = new ArrayList<HashMap<String,Object>>();
        
        for( Issue issue : issues ) {     
            HashMap<String,Object> temp = new HashMap<String,Object>();
            temp.put("issue_title", issue.getTitle());
            temp.put("issue_date", issue.getDate());
            list_populate.add(temp);
        }
                        
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), list_populate,
                R.layout.issue_row,
                new String[] {"issue_title", "issue_date"},
                new int[] { R.id.issue_title, R.id.issue_date});
        return adapter;
	}
}