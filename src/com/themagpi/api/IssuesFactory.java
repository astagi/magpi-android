package com.themagpi.api;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
        SimpleDateFormat format = new SimpleDateFormat("LLLL yyyy", Locale.US);
        String dateLocale = item.getTitle().split(" - ")[1];
        try {
			Date instance = format.parse(dateLocale);
			dateLocale = (new DateFormatSymbols()).getMonths()[instance.getMonth()] + " " + (1900 + instance.getYear());
			dateLocale = dateLocale.substring(0,1).toUpperCase() + dateLocale.substring(1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return (new Issue.Builder())
                .id(item.getTitle().split(" - ")[0].replace(" ", "_"))
                .date(dateLocale)
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
