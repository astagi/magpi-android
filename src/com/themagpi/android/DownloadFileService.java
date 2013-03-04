package com.themagpi.android;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.themagpi.api.Issue;

public class DownloadFileService extends Service {

    public static final String BROADCAST_STATUS = "com.themagpi.android.downloadfileservice";
    protected static final int ERROR = 0;
    protected static final int UPDATE = 2;
    protected static final int COMPLETE = 1;
    
    private volatile boolean isRunning = false;
    
    RetreiveFileTask task;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task = new RetreiveFileTask((Issue)(intent.getParcelableExtra("IssueObject")), 
                intent.getBooleanExtra("keep", false));
        task.execute();
        isRunning = true;
        return 0;
    }
    
    public void onDestroy() {
        if(task != null) {
            isRunning = false;
            task.cancel(true);
        }
    }
    
    private void downloadFile(Issue issue, boolean keep) {
                
        Log.e("URL to download", issue.getPdfUrl());
        
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = null;
        File file = null;
        if(!keep) {
            dir = new File (sdCard.getAbsolutePath() + "/MagPi/");
            dir.mkdirs();
            file = new File(dir, "tmp.pdf");
        } else {
            dir = new File (sdCard.getAbsolutePath() + "/MagPi/" + issue.getId());
            dir.mkdirs();
            file = new File(dir, issue.getId() + ".pdf");
        }
        
        int actualRead = 0;
        long fileSize = 0;

        try {
            
            actualRead = 0;
            fileSize = 0;

            URL url = new URL(issue.getPdfUrl());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
 
            String contentLen = urlConnection.getHeaderField("Content-Length");
            
            if(contentLen == null)
                throw new IOException();
                    
            fileSize = Long.parseLong(contentLen);
            
            Log.e("File length", "" + fileSize);
            
            int percentage, oldPercentage = 0;
    
            InputStream inputStr = new BufferedInputStream(urlConnection.getInputStream());
            StatisticsInputStream input = new StatisticsInputStream(inputStr);
            FileOutputStream output = new FileOutputStream(file);
    
            byte data[] = new byte[1024];
            int count = 0;
            
            while (isRunning && (count = input.read(data)) != -1) {
                output.write(data);
                actualRead += count;
                percentage = (int)(((float)actualRead/fileSize)*100);
                if(percentage != oldPercentage) {
                    sendPercentage(percentage);
                    oldPercentage = percentage;
                }
            }
    
            output.flush();
            output.close();
            input.close();
            
            if (actualRead == fileSize)
                sendDownloadComplete(file);
            
        } catch (IOException e) {
            sendError();
        } finally {
            if(actualRead != fileSize) {
                Log.e("ROLLBACK", "ROLLBACK");
                file.delete();
            }
        }
            
    }
    
    private void sendError() {  
        Intent intent = new Intent();
        intent.setAction(BROADCAST_STATUS);
        intent.putExtra("status", ERROR);
        sendBroadcast(intent);
    }
    
    private void sendDownloadComplete(File file) {  
        Intent intent = new Intent();
        intent.setAction(BROADCAST_STATUS);
        intent.putExtra("status", COMPLETE);
        intent.putExtra("file", file);
        sendBroadcast(intent);
    }

    private void sendPercentage(int value) {
        Intent intent = new Intent();
        intent.setAction(BROADCAST_STATUS);
        intent.putExtra("status", UPDATE);
        intent.putExtra("percentage", value);
        sendBroadcast(intent);
    }

    class RetreiveFileTask extends AsyncTask<Void, Void, Void> {

        private Issue issue;
        private boolean keep;

        RetreiveFileTask(Issue issue, boolean keep) {
            this.issue = issue;
            this.keep = keep;
        }

        protected void onPostExecute() {
            
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            downloadFile(issue, keep);
            return null;
        }
     }

}
