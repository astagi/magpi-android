package com.themagpi.api;

import java.util.ArrayList;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

public class NewsFactory {
	
    public static ArrayList<News> buildFromRSSFeed(RSSFeed feed) {
        
        ArrayList<News> news = new ArrayList<News>();
        
        for(RSSItem item : feed.getItems()) {
            news.add(new News(item.getPubDate().toString(), item.getDescription()));
        }
        
        return news;
    }
}
