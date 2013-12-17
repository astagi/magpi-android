package com.themagpi.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

public class NewsFactory {
    
    public static ArrayList<News> buildFromRSSFeed(RSSFeed feed) {
        
        ArrayList<News> news = new ArrayList<News>();
        
        for(RSSItem item : feed.getItems()) {
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy HH:mm");
            String pubDateText = formatter.format(item.getPubDate());
            String content = item.getDescription().replace("href=\"/l.php?u=", "href=\"http://www.facebook.com/l.php?u=");
            news.add(new News(pubDateText, content));
        }
        
        return news;
    }
}
