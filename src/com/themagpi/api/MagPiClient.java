package com.themagpi.api;

import java.util.ArrayList;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MagPiClient {
    
    private AsyncHttpClient client = new AsyncHttpClient();

    public static class OnIssuesReceivedListener {
        public void onReceived(ArrayList<Issue> issues) {}
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
    
    public void getNews(AsyncHttpResponseHandler asyncHandler) {
        //TODO later : "http://feeds.feedburner.com/MagPi"
    }


    
}
