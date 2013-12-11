package com.themagpi.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.themagpi.android.R;
import com.themagpi.api.News;

public class LinkedAdapter extends BaseAdapter {
    
    private class ViewHolder {
        public WebView webView;
        public TextView  textView;
    }
    
    private ArrayList<News> news;
    private LayoutInflater  inflater;
    
    public LinkedAdapter(Context context, ArrayList<News> news) {
    	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	this.news = news;
    }

    @Override
    public int getCount() {
        if (news != null)
            return news.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (news != null && position >= 0 && position < getCount()) {
            return news.get(position);
        }
        
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        View view = convertView;
        ViewHolder viewHolder;

        News newsModel = news.get(position);
        
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_news_item, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.webView = (WebView) view.findViewById(R.id.news_content);
            viewHolder.textView  = (TextView) view.findViewById(R.id.news_date);
            
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        
        viewHolder.webView.loadData(newsModel.getContent(), "text/html; charset=utf-8", "utf-8");
        viewHolder.textView.setText(newsModel.getDate());
        
        return view;
    }

}