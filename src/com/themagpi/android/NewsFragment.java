package com.themagpi.android;

import java.util.ArrayList;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;
import com.themagpi.api.News;

public class NewsFragment extends SherlockListFragment {
    ArrayList<News> news = new ArrayList<News>();
    MagPiClient client = new MagPiClient();
    int layout;

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(Issue issue);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

    }
    

    @Override
    public void onStart() {
        super.onStart();

        if (getFragmentManager().findFragmentById(R.id.issue_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        
        client.getNews(new MagPiClient.OnNewsReceivedListener() {
            public void onReceived(ArrayList<News> news) {         
                //showPdf(issues.get(issues.size() - 1));
                NewsFragment.this.news = news;
                try {
                    setListAdapter(new ArrayAdapter<News>(getActivity(), layout, news));   
                } catch (NullPointerException ex) {}
            }
        });
        
        ((SherlockFragmentActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
}