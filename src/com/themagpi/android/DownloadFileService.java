package com.themagpi.android;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class DownloadFileService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        (new RetreiveFileTask(intent)).execute();
        return 0;
    }
    
    private void downloadFile(Intent intent) {
        String remoteUrl = intent.getStringExtra("url");
        String path = intent.getStringExtra("path");
        
        Log.e("URL to download", remoteUrl);

        try {
            
            URL url = new URL(remoteUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
 
            long fileSize = Long.parseLong(urlConnection.getHeaderField("Content-Length"));
            
            Log.e("File length", "" + fileSize);
    
            InputStream inputStr = new BufferedInputStream(urlConnection.getInputStream());
            StatisticsInputStream input = new StatisticsInputStream(inputStr);
            OutputStream output = new FileOutputStream(path);
    
            byte data[] = new byte[1024];
            int count = 0;
            long actualRead = 0;
            
            while ((count = input.read(data)) != -1) {
                output.write(data);
                actualRead += count;
                sendPercentage((actualRead/count)*100);
            }
    
            output.flush();
            output.close();
            input.close();
            
            sendDownloadComplete();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
            
       
    }
    
    private void sendDownloadComplete() {
        Log.e("Download progress", "COMPLETE");
        
    }

    private void sendPercentage(long value) {
        Log.e("Download progress", value + "%");
        
    }

    class RetreiveFileTask extends AsyncTask<Void, Void, Void> {

        private Intent intent;

        RetreiveFileTask(Intent intent) {
            this.intent = intent;
        }
        

        protected void onPostExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            downloadFile(intent);
            return null;
        }
     }

}
