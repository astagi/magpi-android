package com.themagpi.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.themagpi.adapters.LinkedAdapter;
import com.themagpi.android.R;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;
import com.themagpi.api.News;
import com.themagpi.interfaces.Refreshable;
import com.themagpi.interfaces.RefreshableContainer;

public class NewsFragment extends SherlockFragment implements Refreshable {
    ArrayList<News> news = new ArrayList<News>();
    MagPiClient client = new MagPiClient();
    int layout;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        return view;
    }

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(Issue issue);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    
    public ListView getListView() {
    	return (ListView) getActivity().findViewById(R.id.news_list_view);
    }

    @Override
    public void refresh() {
        ((RefreshableContainer) getActivity()).startRefreshIndicator();
        client.getNews(new MagPiClient.OnNewsReceivedListener() {
            public void onReceived(ArrayList<News> news) {         
                NewsFragment.this.news = news;
                getListView().setAdapter(createNewsAdapter(news));
                ((RefreshableContainer) getActivity()).stopRefreshIndicator();
                getActivity().findViewById(R.id.progress_news).setVisibility(View.GONE);
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
                //Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_LONG).show();
            }
        });        
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
                R.layout.adapter_news_item,
                new String[] {"news_content", "news_date"},
                new int[] { R.id.news_content, R.id.news_date},
                new int[] { R.id.news_content});
        return adapter;
    }
}