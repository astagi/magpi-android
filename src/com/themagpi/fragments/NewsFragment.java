package com.themagpi.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

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
    
    public ListView getListView() {
    	return (ListView) getActivity().findViewById(R.id.news_list_view);
    }

    @Override
    public void refresh() {
        ((RefreshableContainer) getActivity()).startRefreshIndicator();
        client.getNews(getActivity(), new MagPiClient.OnNewsReceivedListener() {
            public void onReceived(ArrayList<News> news) {         
                NewsFragment.this.news = news;
                getListView().setAdapter(createNewsAdapter(news));
                ((RefreshableContainer) getActivity()).stopRefreshIndicator();
                getActivity().findViewById(R.id.progress_news).setVisibility(View.GONE);
            }
            public void onError(int error) {
                ((RefreshableContainer) getActivity()).stopRefreshIndicator();
                //Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_LONG).show();
            }
        });        
    }
    
    public BaseAdapter createNewsAdapter(ArrayList<News> news) {                        
        LinkedAdapter adapter = new LinkedAdapter(getActivity(), news);
        return adapter;
    }
}