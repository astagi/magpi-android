package com.themagpi.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import android.content.Intent;
import android.util.Log;

public class IssuesFactory {
    
    public static Issue buildFromJsonObject(JSONObject item) {
        try {
            return (new Issue.Builder())
                    .id("" + item.getInt("id"))
                    .date(capitalizeString(item.getString("date")))
                    .title("Issue " + item.getInt("id"))
                    .pdfUrl(item.getString("url"))
                    .imageUrl(item.getString("cover"))
                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ArrayList<Issue> buildFromJSONFeed(JSONObject feed) {
        
        ArrayList<Issue> issues = new ArrayList<Issue>();
        JSONArray jsonArray;
        
        try {
            jsonArray = feed.getJSONArray("issues");      
            for(int i = 0 ; i < jsonArray.length() ; i++) {
                issues.add(buildFromJsonObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return issues;
    }
    
    public static Issue buildFromRSSItem(RSSItem item) {
        
        Pattern IMAGE_PATTERN = Pattern.compile("img src='(.*?)'");
        
        Matcher mImg = IMAGE_PATTERN.matcher(item.getDescription());
        String imgUrl = "";
        while (mImg.find()) {
            imgUrl = mImg.group(1);
        }
        
        Pattern ISSUE_PATTERN = Pattern.compile("The-MagPi-(issue-[0-9]+)-en");
        
        Matcher mIssue = ISSUE_PATTERN.matcher(item.getTitle());
        String issueTitle = "";
        while (mIssue.find()) {
            issueTitle = mIssue.group(1);
            issueTitle = issueTitle.replace("-", " ");
            Log.e("ISSUETITLEEE", issueTitle);
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");
        String pubDateText = formatter.format(item.getPubDate());
        Log.e("ISSUEDATEEEE", "" + item.getPubDate());
        
        return (new Issue.Builder())
                .id(issueTitle.replace(" ", "_"))
                .date(capitalizeString(pubDateText))
                .title(capitalizeString(issueTitle))
                .pdfUrl(item.getLink().toString() + "/pdf")
                .imageUrl(imgUrl)
                .build();
    }
    
    public static Issue buildFromIntent(Intent item) {
        
        Pattern ISSUE_PATTERN = Pattern.compile("The-MagPi-(issue-[0-9]+)-en");
        
        Matcher mIssue = ISSUE_PATTERN.matcher(item.getStringExtra("title"));
        String issueTitle = "";
        while (mIssue.find()) {
            issueTitle = mIssue.group(1);
            issueTitle = issueTitle.replace("-", " ");
            Log.e("ISSUETITLEEE", issueTitle);
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        Date date = null;
        try {
            date = formatter.parse(item.getStringExtra("pubDate"));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat("MMMM yyyy");
        String pubDateText = formatter2.format(date);
        
        return (new Issue.Builder())
                .id(issueTitle.replace(" ", "_"))
                .date(capitalizeString(pubDateText))
                .title(capitalizeString(issueTitle))
                .pdfUrl(item.getStringExtra("link") + "/pdf")
                .imageUrl(item.getStringExtra("image"))
                .build();
    }
    
    
    public static String capitalizeString(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
    }
    
    public static ArrayList<Issue> buildFromRSSFeed(RSSFeed feed) {
        
        ArrayList<Issue> issues = new ArrayList<Issue>();
        
        for(RSSItem item : feed.getItems()) {
            issues.add(buildFromRSSItem(item));
        }
        
        return issues;
    }
}
