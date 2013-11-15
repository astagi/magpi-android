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
import com.loopj.android.http.JsonHttpResponseHandler;

/*
 * NEW API
 * Parameters are set in a query string to http://www.themagpi.com/mps_api/mps-api-v1.php
 * Parameters to set:
 * mode = list_issues | list_articles
 * issue_id
 * html = true | false : Optional, defaults to false.
 */

public class MagPiClient {
    
    private AsyncHttpClient client = new AsyncHttpClient();

    public static interface OnIssuesReceivedListener {
        public abstract void onReceived(ArrayList<Issue> issues);
        public abstract void onError(int error);
    }
    
    public static interface OnNewsReceivedListener {
        public abstract void onReceived(ArrayList<News> news);
        public abstract void onError(int error);
    }
    
    public static interface OnFileReceivedListener {
        public abstract void onReceived(byte[] fileData);
        public abstract void onError(int error);
    }
    
    public void getIssues(final OnIssuesReceivedListener issueListener) {
        client.get("http://www.themagpi.com/mps_api/mps-api-v1.php?mode=list_issues", new JsonHttpResponseHandler() {
            
            @Override
            public void onSuccess(JSONObject response) {
            	try {
                	Log.e("RESPONSE", response.toString());
                	issueListener.onReceived(IssuesFactory.buildFromJSONFeed(response));
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
            
            @Override
            public void onFailure(Throwable e, JSONObject response) {
            	try {
                	issueListener.onError(0);
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
            	try {
                	issueListener.onError(0);
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
        });
    }
    
    public void getNews(final OnNewsReceivedListener newsListener) {
        client.get("http://feeds.feedburner.com/MagPi", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	try {
	                RSSParser parser = new RSSParser(new RSSConfig());
	                RSSFeed feed = parser.parse(new ByteArrayInputStream(response.getBytes()));
	                newsListener.onReceived(NewsFactory.buildFromRSSFeed(feed));
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
            	try {
                	newsListener.onError(0);
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
        });
    }
    
    public void close(Context ctx) {
        client.cancelRequests(ctx, true);
    }
}
