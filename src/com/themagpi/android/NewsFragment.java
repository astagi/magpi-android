package com.themagpi.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;
import com.themagpi.api.News;

public class NewsFragment extends SherlockListFragment implements Refreshable {
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
        this.setHasOptionsMenu(true);
        refresh();

    }
    

    @Override
    public void onStart() {
        super.onStart();
        ((SherlockFragmentActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    
    public void onPause() {
        super.onPause();
        if(getActivity() != null && client != null)
            client.close(getActivity());
    }

    @Override
    public void refresh() {
        client.getNews(new MagPiClient.OnNewsReceivedListener() {
            public void onReceived(ArrayList<News> news) {         
                NewsFragment.this.news = news;
                try {
                    setListAdapter(createNewsAdapter(news));
                    ((RefreshableContainer) getActivity()).stopRefreshIndicator();
                } catch (NullPointerException ex) {}
                getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                            int position, long arg3) {
                        
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, NewsFragment.this.news.get(position).getContent());
                        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_news)));
                        return true;
                    }
                });
            }
            public void onError(int error) {
                ((RefreshableContainer) getActivity()).stopRefreshIndicator();
            }
        });
                
        ((RefreshableContainer) getActivity()).startRefreshIndicator();
        
    }
    
    public SimpleAdapter createNewsAdapter(ArrayList<News> news) {
        ArrayList<HashMap<String,Object>> list_populate = new ArrayList<HashMap<String,Object>>();
        
        for( News pnews : news ) {     
            HashMap<String,Object> temp = new HashMap<String,Object>();
            temp.put("news_content", pnews.getContent());
            temp.put("news_date", pnews.getDate());
            list_populate.add(temp);
        }
                        
        LinkedAdapter adapter = new LinkedAdapter(getActivity(), list_populate,
                R.layout.news_row,
                new String[] {"news_content", "news_date"},
                new int[] { R.id.news_content, R.id.news_date},
                new int[] { R.id.news_content});
        return adapter;
    }
}