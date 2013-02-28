package com.themagpi.android;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context ctx, String devId) {
		Log.e("ERROR", "ERROR" + devId);	
	}

	@Override
	protected void onMessage(Context ctx, Intent intent) {
		Log.e("MESSAGE", "MESSAGE" + intent.getStringExtra("IntTemperature"));
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