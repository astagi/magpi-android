package com.themagpi.android;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context ctx, String devId) {
		Log.e("ERROR", "ERROR" + devId);	
	}

	@Override
	protected void onMessage(Context ctx, Intent intent) {
		Log.e("type", "::" + intent.getStringExtra("type"));
		Log.e("title", "::" + intent.getStringExtra("title"));
		Log.e("link", "::" + intent.getStringExtra("link"));
		Log.e("image", "::" + intent.getStringExtra("image"));
		//Log.e("icon", "::" + intent.getStringExtra("imageb64"));
		
		InputStream is = null; 
		byte[] bytes = null;
		
		try { 
			URL url = new URL(intent.getStringExtra("image")); 
			is = url.openStream(); 
			int buff = 0; 
			ByteArrayOutputStream outStream = new ByteArrayOutputStream(); 
			while((buff = is.read()) != -1) { 
				outStream.write(buff); 
			} 
			bytes = outStream.toByteArray(); 
		} catch(Exception e)	{	}  
		
		Notification noti = new NotificationCompat.Builder(this)
			 .setContentTitle("New issue!!")
			 .setContentText(intent.getStringExtra("title"))
			 .setSmallIcon(R.drawable.new_issue)
			 .setLargeIcon(BitmapFactory.decodeByteArray(bytes, 0 , bytes.length))
			 .getNotification();
		
    	/*AsyncHttpClient client = new AsyncHttpClient();
    	String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
    	client.get(intent.getStringExtra("image_small"), new BinaryHttpResponseHandler(allowedContentTypes) {
            @SuppressLint("NewApi") @Override
            public void onSuccess(byte[] data) {
            	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            		noti.largeIcon = BitmapFactory.decodeByteArray(data, 0, data.length);;
            }
            
            
        });*/
		
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