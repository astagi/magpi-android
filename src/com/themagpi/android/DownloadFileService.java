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
    protected static final int STOP = 0;
    protected static final int UPDATE = 2;
    protected static final int COMPLETE = 1;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        (new RetreiveFileTask((Issue)(intent.getParcelableExtra("IssueObject")))).execute();
        return 0;
    }
    
    private void downloadFile(Issue issue) {
                
        Log.e("URL to download", issue.getPdfUrl());

        try {
            
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/MagPi/" + issue.getId());
            dir.mkdirs();
            File file = new File(dir, issue.getId() + ".pdf");
        
            URL url = new URL(issue.getPdfUrl());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
 
            long fileSize = Long.parseLong(urlConnection.getHeaderField("Content-Length"));
            
            Log.e("File length", "" + fileSize);
            
            int percentage, oldPercentage = 0;
    
            InputStream inputStr = new BufferedInputStream(urlConnection.getInputStream());
            StatisticsInputStream input = new StatisticsInputStream(inputStr);
            FileOutputStream output = new FileOutputStream(file);
    
            byte data[] = new byte[1024];
            int count = 0;
            int actualRead = 0;
            
            while ((count = input.read(data)) != -1) {
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
            
            sendDownloadComplete();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /*Intent intentPdf = new Intent(Intent.ACTION_VIEW);
        intentPdf.setDataAndType(Uri.fromFile(file), "application/pdf");
        startActivity(intentPdf);*/
            
    }
    
    private void sendDownloadComplete() {  
        Intent intent = new Intent();
        intent.setAction(BROADCAST_STATUS);
        intent.putExtra("status", COMPLETE);
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

        RetreiveFileTask(Issue issue) {
            this.issue = issue;
        }

        protected void onPostExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            downloadFile(issue);
            return null;
        }
     }

}
