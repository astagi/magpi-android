package com.themagpi.api;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import android.util.Log;

public class IssuesFactory {
    
    public static Issue buildFromRSSItem(RSSItem item) {
        
        Pattern IMAGE_PATTERN = Pattern.compile("img src='(.*?)'");
        
        Matcher m = IMAGE_PATTERN.matcher(item.getDescription());
        String imgUrl = "";
        while (m.find()) {
            imgUrl = m.group(1);
        }
        return (new Issue.Builder())
                .id(item.getTitle().split(" - ")[0].replace(" ", "_"))
                .date(item.getTitle().split(" - ")[1])
                .title(item.getTitle().split(" - ")[0])
                .pdfUrl(item.getLink().toString())
                .imageUrl(imgUrl)
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
