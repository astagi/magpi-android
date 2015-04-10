package com.themagpi.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                    .id(item.getString("title"))
                    .date(capitalizeString(item.getString("date")))
                    .title("Issue " + item.getString("title"))
                    .pdfUrl(item.getString("pdf"))
                    .imageUrl(item.getString("cover"))
                    .editorial(item.getString("editorial"))
                    .issuuUrl(item.getString("issuu"))
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
            jsonArray = feed.getJSONArray("data");      
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
        return (new Issue.Builder())
                .id(item.getStringExtra("id"))
                .date(item.getStringExtra("date"))
                .title("Issue " + item.getStringExtra("title"))
                .pdfUrl(item.getStringExtra("link"))
                .imageUrl(item.getStringExtra("image"))
                .editorial(item.getStringExtra("editorial"))
                .issuuUrl(item.getStringExtra("issuu"))
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
