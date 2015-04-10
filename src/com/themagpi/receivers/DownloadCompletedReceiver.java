package com.themagpi.receivers;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.themagpi.android.R;
import com.themagpi.android.services.PebbleNotifier;

public class DownloadCompletedReceiver extends BroadcastReceiver {
	
	private static int ID_NOTIFICATION = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            Query query = new Query();
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor c = dm.query(query);
            if (c.moveToFirst()) {
                int titleColumnIndex = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
                String title = context.getString(R.string.menu_view) + " " + c.getString(titleColumnIndex);
                int pdfPathColumnIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String pdfPath = c.getString(pdfPathColumnIndex);
                Intent intentPdf = new Intent(Intent.ACTION_VIEW);
                intentPdf.setDataAndType(Uri.parse(pdfPath), "application/pdf");
        		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
        				intentPdf, PendingIntent.FLAG_CANCEL_CURRENT);
        		
        		SharedPreferences settings = context.getSharedPreferences("IssueDownloadNotification", Context.MODE_PRIVATE);
        		if(settings.getBoolean(title, false))
        			return;
        		settings.edit().putBoolean(title, true).commit();

        		Notification noti = new NotificationCompat.Builder(context)
        				.setContentTitle(title)
        				.setContentText(context.getString(R.string.download_completed_text))
        				.setContentIntent(contentIntent)
        				.setSmallIcon(R.drawable.new_issue)
        				.getNotification();
        		
        		PebbleNotifier.notify(context, title, context.getString(R.string.download_completed_text));

        		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        		noti.flags |= Notification.FLAG_AUTO_CANCEL;
        		notificationManager.notify(ID_NOTIFICATION++, noti);
            }
            c.close();
        }
	}
}