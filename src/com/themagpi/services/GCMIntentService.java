package com.themagpi.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.themagpi.activities.IssueDetailsActivity;
import com.themagpi.android.Config;
import com.themagpi.android.R;
import com.themagpi.android.R.drawable;
import com.themagpi.api.Issue;
import com.themagpi.api.IssuesFactory;
import com.themagpi.fragments.IssueDetailsFragment;

public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
        super(Config.SENDER_ID);
    }
    
    @Override
    protected void onError(Context ctx, String devId) {
        Log.e("ERROR", "ERROR" + devId);    
    }

    @Override
    protected void onMessage(Context ctx, Intent intent) {
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (!prefs.getBoolean("pref_notif_newissue", true))
            return;
        
        Issue issue = IssuesFactory.buildFromIntent(intent);
        
        if (prefs.getString("last_issue", "").equalsIgnoreCase(issue.getId()))
            return;
        
        InputStream is = null; 
        byte[] bytes = null;
        
        try { 
            URL url = new URL(issue.getCoverUrl()); 
            is = url.openStream(); 
            int buff = 0; 
            ByteArrayOutputStream outStream = new ByteArrayOutputStream(); 
            while((buff = is.read()) != -1) { 
                outStream.write(buff); 
            } 
            bytes = outStream.toByteArray(); 
        } catch(Exception e)    {   }  
        
        Intent notificationIntent = new Intent(this, IssueDetailsActivity.class);
        notificationIntent.putExtra(IssueDetailsFragment.ARG_ISSUE, issue);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        
        Notification noti = new NotificationCompat.Builder(this)
             .setContentTitle("New issue!")
             .setContentText(issue.getTitle() + " - " + issue.getDate())
             .setContentIntent(contentIntent)
             .setSmallIcon(R.drawable.new_issue)
             .setLargeIcon(BitmapFactory.decodeByteArray(bytes, 0 , bytes.length))
             .getNotification();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti); 
        
    }

    @Override 
    protected void onRegistered(Context ctx, String devId) {
        RequestParams params = new RequestParams();
        params.put("id", devId);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Config.SERVICE_URL + "/register", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray timeline) {

            }
        });
    }

    @Override
    protected void onUnregistered(Context ctx, String devId) {

    }

}