package com.themagpi.api;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.json.JSONObject;
import org.mcsoxford.rss.RSSConfig;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSParser;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

/*
 * NEW API
 * Get the issue pdf: http://www.themagpi.com/en/issue/%d/pdf/
 * Get the issue img: http://www.themagpi.com/assets/%d.jpg
 */

public class MagPiClient {
    
    private AsyncHttpClient client = new AsyncHttpClient();
    private final int LAST_ISSUE = 16;

    public static abstract class OnIssuesReceivedListener {
        public abstract void onReceived(ArrayList<Issue> issues);
        public abstract void onError(int error);
    }
    
    public static abstract class OnNewsReceivedListener {
        public abstract void onReceived(ArrayList<News> news);
        public abstract void onError(int error);
    }
    
    public static abstract class OnFileReceivedListener {
        public abstract void onReceived(byte[] fileData);
        public abstract void onError(int error);
    }
    
    public void getIssues(final OnIssuesReceivedListener issueListener) {
    	ArrayList<Issue> issues = new ArrayList<Issue>();
    	for(int i = 1 ; i <= LAST_ISSUE; i++) {
    		Issue issue = new Issue.Builder()
            .id("" + i)
            .date("")
            .title("Issue " + i)
            .pdfUrl(String.format("http://www.themagpi.com/en/issue/%d/pdf/", i))
            .imageUrl(String.format("http://www.themagpi.com/assets/%d.jpg", i))
            .build();
            issues.add(issue);
    	}
    	issueListener.onReceived(issues);
        /*client.get("http://magpiapi.herokuapp.com/issues", new JsonHttpResponseHandler() {
            
            @Override
            public void onSuccess(JSONObject response) {
                Log.e("RESPONSE", response.toString());
                issueListener.onReceived(IssuesFactory.buildFromJSONFeed(response));
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
                issueListener.onError(0);
            }
        });*/
    }
    
    public void getPdf(Issue issue, final OnFileReceivedListener fileListener) {
        Log.e("DOWNLOADING", "..." + issue.getPdfUrl());
        String[] allowedContentTypes = new String[] { "application/pdf" };
        client.get(issue.getPdfUrl(), new BinaryHttpResponseHandler(allowedContentTypes) {
            @Override
            public void onSuccess(byte[] fileData) {
                fileListener.onReceived(fileData);
            }
        });
    }
    
    public void getCover(Issue issue, final OnFileReceivedListener fileListener) {
        String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
        client.get(issue.getCoverUrl(), new BinaryHttpResponseHandler(allowedContentTypes) {
            @Override
            public void onSuccess(byte[] fileData) {
                fileListener.onReceived(fileData);
            }
            
            
        });
    }
    
    public void getNews(final OnNewsReceivedListener newsListener) {
        client.get("http://feeds.feedburner.com/MagPi", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                RSSParser parser = new RSSParser(new RSSConfig());
                RSSFeed feed = parser.parse(new ByteArrayInputStream(response.getBytes()));
                newsListener.onReceived(NewsFactory.buildFromRSSFeed(feed));
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
                newsListener.onError(0);
            }
        });
    }
    
    public void close(Context ctx) {
        client.cancelRequests(ctx, true);
    }
}
