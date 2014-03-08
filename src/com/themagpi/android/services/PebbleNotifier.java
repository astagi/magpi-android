package com.themagpi.android.services;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

public class PebbleNotifier {
	
	public static void notify(Context ctx, String title, String body) {
		final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");
        final Map data = new HashMap();
        data.put("title", title);
        data.put("body", body);
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();
        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "The MagPi");
        i.putExtra("notificationData", notificationData);
        ctx.sendBroadcast(i);
	}

}
