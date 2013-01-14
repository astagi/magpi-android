package com.themagpi.api;

import java.util.ArrayList;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

public class IssuesFactory {
    
    public static Issue buildFromRSSItem(RSSItem item) {
        return (new Issue.Builder())
                .pdfUrl(item.getLink().toString())
                .build();
    }
    
    public static ArrayList<Issue> buildFromRSSFeed(RSSFeed feed) {
        
        ArrayList<Issue> issues = new ArrayList<Issue>();
        
        for(RSSItem item : feed.getItems()) {
            issues.add(buildFromRSSItem(item));
        }
        
        return issues;
    }
}
