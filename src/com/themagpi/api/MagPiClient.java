package com.themagpi.api;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class MagPiClient {
    
    private AsyncHttpClient client = new AsyncHttpClient();

    public static class OnIssuesReceivedListener {
        public void onReceived(ArrayList<Issue> issues) {}
        public void onError() {}
    }
    
    public static class OnFileReceivedListener {
        public void onReceived(byte[] fileData) {}
        public void onError() {}
    }
    
    public void getIssues(final OnIssuesReceivedListener issueListener) {
        client.get("http://feeds.feedburner.com/themagpi", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                RSSReader reader = new RSSReader();
                RSSFeed feed = reader.loadFromString(response);                
                issueListener.onReceived(IssuesFactory.buildFromRSSFeed(feed));
                reader.close();
            }
        });
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
    
    public void getNews(AsyncHttpResponseHandler asyncHandler) {
        //TODO later : "http://feeds.feedburner.com/MagPi"
    }
    
    public void close(Context ctx) {
        client.cancelRequests(ctx, true);
    }
}
