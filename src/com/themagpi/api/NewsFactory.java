package com.themagpi.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import android.text.format.DateFormat;

public class NewsFactory {
	
    public static ArrayList<News> buildFromRSSFeed(RSSFeed feed) {
        
        ArrayList<News> news = new ArrayList<News>();
        
        for(RSSItem item : feed.getItems()) {
        	SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy HH:mm");
        	String pubDateText = formatter.format(item.getPubDate());
            news.add(new News(pubDateText, item.getDescription()));
        }
        
        return news;
    }
}
