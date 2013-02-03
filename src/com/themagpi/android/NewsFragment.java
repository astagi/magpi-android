package com.themagpi.android;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.themagpi.api.News;

public class NewsFragment extends SherlockListFragment implements Refreshable {
    ArrayList<News> news = new ArrayList<News>();
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
    }
    

    @Override
    public void onStart() {
        super.onStart();

        if (getFragmentManager().findFragmentById(R.id.issue_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        
        ((SherlockFragmentActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        
        refresh();
    }
    
    public void onPause() {
        super.onPause();
        if(getActivity() != null && client != null)
            client.close(getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //getListView().setItemChecked(position, true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    	this.menu = menu;
        this.inflater = (LayoutInflater) ((SherlockFragmentActivity) getActivity()).getSupportActionBar().getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	@Override
	public void refresh() {
        client.getNews(new MagPiClient.OnNewsReceivedListener() {
            public void onReceived(ArrayList<News> news) {         
                NewsFragment.this.news = news;
                try {
                    setListAdapter(createNewsAdapter(news));
                    if(menu != null)
                    	menu.findItem(R.id.menu_refresh).setActionView(null);
                } catch (NullPointerException ex) {}
            }
        });
                
        if(menu != null)
        	menu.findItem(R.id.menu_refresh).setActionView(inflater.inflate(R.layout.actionbar_refresh_progress, null));
		
	}
	
	public SimpleAdapter createNewsAdapter(ArrayList<News> news) {
		ArrayList<HashMap<String,Object>> list_populate = new ArrayList<HashMap<String,Object>>();
        
        for( News pnews : news ) {     
            HashMap<String,Object> temp = new HashMap<String,Object>();
            temp.put("news_content", pnews.getContent());
            temp.put("news_date", pnews.getDate());
            list_populate.add(temp);
        }
                        
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), list_populate,
                R.layout.news_row,
                new String[] {"news_content", "news_date"},
                new int[] { R.id.news_content, R.id.news_date});
        return adapter;
	}
}