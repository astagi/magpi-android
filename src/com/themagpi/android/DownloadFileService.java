package com.themagpi.android;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class DownloadFileService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        (new RetreiveFileTask(intent)).execute();
        return START_STICKY;
    }
    
    private void downloadFile(Intent intent) {
        String remoteUrl = intent.getStringExtra("url");
        String path = intent.getStringExtra("path");
        
        Log.e("URL", "" + remoteUrl);

        

        try {
            
            URL url = new URL(remoteUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            
            Log.e("CONTENTLEN", "boh" + urlConnection.getHeaderField("Content-Length"));
    
            /*InputStream inputStr = new BufferedInputStream(urlConnection.getInputStream());
            StatisticsInputStream input = new StatisticsInputStream(inputStr);
            OutputStream output = new FileOutputStream(path);
    
            byte data[] = new byte[1024];
    
            int count = 0;
            while ((count = input.read(data)) != -1) {
                output.write(data);
            }
    
            output.flush();
            output.close();
            input.close();*/
            
        } catch (IOException e) {
            e.printStackTrace();
        }
            
       
    }
    
    class RetreiveFileTask extends AsyncTask<Void, Void, Void> {

        private Intent intent;

        RetreiveFileTask(Intent intent) {
            this.intent = intent;
        }
        

        protected void onPostExecute() {
            // TODO: check this.exception 
            // TODO: do something with the feed
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            downloadFile(intent);
            return null;
        }
     }

}
