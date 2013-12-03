package com.themagpi.android;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.themagpi.activities.IssueDetailsActivity;
import com.themagpi.api.Issue;
import com.themagpi.api.IssuesFactory;
import com.themagpi.api.MagPiClient;
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
		
		Log.e("GCM", "MESSAGE ARRIVED " + intent.getExtras());
		Log.e("LOG", "LAST ISSUE FETCHED " + prefs.getString("last_issue", ""));

		if (!prefs.getBoolean("pref_notif_newissue", true))
			return;

		Issue issue = IssuesFactory.buildFromIntent(intent);

		if (prefs.getString("last_issue", "").equalsIgnoreCase(issue.getId())) {
			return;
		} else {
			prefs.edit().putString("last_issue", issue.getId()).commit();
		}

		InputStream is = null;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] notificationImgData = stream.toByteArray();

		try {
			URL url = new URL(issue.getCoverUrl());
			is = url.openStream();
			int buff = 0;
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			while ((buff = is.read()) != -1) {
				outStream.write(buff);
			}
			notificationImgData = outStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent notificationIntent = new Intent(this, IssueDetailsActivity.class);
		notificationIntent.putExtra(IssueDetailsFragment.ARG_ISSUE, issue);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		Notification noti = new NotificationCompat.Builder(this)
				.setContentTitle(getString(R.string.notification_new_issue))
				.setContentText(issue.getTitle() + " - " + issue.getDate())
				.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.new_issue)
				.setLargeIcon(BitmapFactory.decodeByteArray(notificationImgData, 0, notificationImgData.length))
				.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);
		notifyOnPebble(issue);
	}
	
	private void notifyOnPebble(Issue issue) {
		final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");
        final Map data = new HashMap();
        data.put("title", "The MagPi " + issue.getTitle());
        data.put("body", issue.getEditorial());
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();
        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "The MagPi");
        i.putExtra("notificationData", notificationData);
        sendBroadcast(i);
	}

	@Override
	protected void onRegistered(Context ctx, String idGcm) {
		Log.e("REGISTERED", "REGISTERED");
		new MagPiClient().registerDevice(this, idGcm);
	}

	@Override
	protected void onUnregistered(Context ctx, String devId) {

	}

}