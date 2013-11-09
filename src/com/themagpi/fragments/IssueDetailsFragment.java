package com.themagpi.fragments;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.themagpi.android.R;
import com.themagpi.android.StatisticsInputStream;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;
import com.themagpi.interfaces.Refreshable;
import com.themagpi.interfaces.RefreshableContainer;

public class IssueDetailsFragment extends SherlockFragment implements Refreshable {
    public final static String ARG_ISSUE = "IssueObject";
    private final int MAX_BMP_WIDTH = 600;
    private MagPiClient client = new MagPiClient();
    private ProgressDialog progressBar;
    private Issue issue;
    private Handler updateUI = new Handler();
    private volatile boolean isRunning;
    private RetreiveFileTask task;

    public void onCreate(Bundle si) {
        super.onCreate(si);
        this.setHasOptionsMenu(true);
    }

    @SuppressWarnings("deprecation")
    public void downloadIssue() {
        if(!this.canDisplayPdf(this.getActivity())) {
            Toast.makeText(getActivity(), "You need to install a PDF viewer first!", Toast.LENGTH_LONG).show();
            return;
        }
            
        File pdf = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/MagPi/" + 
                                issue.getId() + "/" + issue.getId() + ".pdf");
        if(pdf.exists()) {
            Intent intentPdf = new Intent(Intent.ACTION_VIEW);
            intentPdf.setDataAndType(Uri.fromFile(pdf), "application/pdf");
            startActivity(intentPdf);
        } else {
            progressBar = new ProgressDialog(this.getActivity());
            progressBar.setCancelable(false);
            progressBar.setMessage(getResources().getString(R.string.downloading) + " ... " + issue.getTitle());
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);
            progressBar.setButton(getResources().getString(R.string.cancel), new ProgressDialog.OnClickListener() {
    
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                    isRunning = false;
                }
    
            });
            progressBar.setMax(100);
            progressBar.show();

            isRunning = true;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(IssueDetailsFragment.this.getSherlockActivity());

            task = new RetreiveFileTask(issue, prefs.getBoolean("pref_store_issue", true));
            task.execute();
            
        }
    }
    
    class RetreiveFileTask extends AsyncTask<Void, Void, Boolean> {

        private Issue issue;
        private boolean keep;

        RetreiveFileTask(Issue issue, boolean keep) {
            this.issue = issue;
            this.keep = keep;
        }

        protected void onPostExecute(Boolean result) {
            if(result != true)
                Toast.makeText(getActivity(), "Error downloading Issue", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            return downloadFile(issue, keep);
        }
     }
    
    private boolean downloadFile(Issue issue, boolean keep) {
        
        boolean result = true;
        
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
                    progressUpdate(percentage);
                    oldPercentage = percentage;
                }
            }
    
            output.flush();
            output.close();
            input.close();
            
            if (actualRead == fileSize) {
                if (file != null) {
                    Intent intentPdf = new Intent(Intent.ACTION_VIEW);
                    intentPdf.setDataAndType(Uri.fromFile(file), "application/pdf");
                    startActivity(intentPdf);
                }
                Log.e("DOWNLOADSERVICE", "COMPLETE");
            }
            
        } catch (IOException e) {
            result = false;
        } finally {
            updateUI.post(new Runnable()
            {
                public void run() 
                {
                    progressDismiss();

                }
            });
            if(actualRead != fileSize) {
                Log.e("ROLLBACK", "ROLLBACK");
                file.delete();
            }
        }
        
        return result;
            
    }
    
    private void progressDismiss() {
        updateUI.post(new Runnable()
        {
            public void run() 
            {
                if(progressBar != null && progressBar.isShowing())
                    progressBar.dismiss();
            }
        });
    }
    
    private void progressUpdate(final int percentage) {
        updateUI.post(new Runnable()
        {
            public void run() 
            {
                if(progressBar != null && progressBar.isShowing())
                    progressBar.setProgress(percentage);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            // mCurrentPosition = savedInstanceState.getInt(ARG_ISSUE);
        }
        return inflater.inflate(R.layout.fragment_issue_details, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        if(issue == null)
            return true;
        switch (item.getItemId()) {
            case R.id.menu_view:
                downloadIssue();
                return true;
            case R.id.menu_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, issue.getPdfUrl());
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_issue)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            issue = (Issue) args.getParcelable("IssueObject");
            updateIssueView(issue);
        }
    }

    public void updateIssueView(Issue issue) {
        this.issue = issue;
        TextView issueText = (TextView) getActivity().findViewById(R.id.article);
        issueText.setText(issue.getTitle() + " - " + issue.getDate());
        TextView editorialText = (TextView) getActivity().findViewById(R.id.text_editorial);
        editorialText.setText(issue.getEditorial());
        DisplayImageOptions options = new DisplayImageOptions.Builder()
        .cacheInMemory()
        .cacheOnDisc().resetViewBeforeLoading()
        .build();
        ImageLoader.getInstance().displayImage(issue.getCoverUrl(), (ImageView) getActivity().findViewById(R.id.cover), options);
        //showCover();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    
    public void onPause() {
        super.onPause(); 
        if (getActivity() != null && client != null)
            client.close(getActivity());
    }
    
    private void showCover() {
        
        final ImageView image = (ImageView) IssueDetailsFragment.this.getActivity().findViewById(R.id.cover);

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/MagPi/"
                + issue.getId());
        dir.mkdirs();
        final File file = new File(dir, "cover.jpg");
        
        if(file.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            image.setImageBitmap(ScaleBitmap(bmp, getBitmapScalingFactor(bmp)));
            ((RefreshableContainer) getActivity()).stopRefreshIndicator();
            getSherlockActivity().findViewById(R.id.image_progress).setVisibility(View.GONE);
            return;
        }

        client.getCover(issue, new MagPiClient.OnFileReceivedListener() {
            public void onReceived(byte[] data) {
                Log.e("File Status", "Arrived");

                try {
                    FileOutputStream f = new FileOutputStream(file);
                    f.write(data);
                    f.flush();
                    f.close();
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    image.setImageBitmap(ScaleBitmap(bmp, getBitmapScalingFactor(bmp)));
                    
                } catch (Exception e) {
                    Log.e("error", "Error opening file.", e);
                } finally {
                    try{
                        ((RefreshableContainer) getActivity()).stopRefreshIndicator();
                        IssueDetailsFragment.this.getSherlockActivity().findViewById(R.id.image_progress).setVisibility(View.GONE);
                    } catch (Exception ex) {}
                }
            }
            
            public void onError(int error) {
                try{
                    ((RefreshableContainer) getActivity()).stopRefreshIndicator();
                    IssueDetailsFragment.this.getSherlockActivity().findViewById(R.id.image_progress).setVisibility(View.GONE);
                } catch (Exception ex) {}
            }
        });
    }

    @Override
    public void refresh() {
        ((RefreshableContainer) getActivity()).startRefreshIndicator(); 
        this.getSherlockActivity().findViewById(R.id.image_progress).setVisibility(View.VISIBLE);
        //showCover();
    }
    
    public boolean canDisplayPdf(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        if (packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    
    /*
     * --------------------------- BITMAP FUNCTIONS ---------------------------
     */
    
    Bitmap ScaleBitmap(Bitmap bm, float scalingFactor) {
        int scaleHeight = (int) (bm.getHeight() * scalingFactor);
        int scaleWidth = (int) (bm.getWidth() * scalingFactor);

        if (scaleWidth <= MAX_BMP_WIDTH)
            return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
        float hwRatio = ((float) bm.getHeight() / bm.getWidth());
        return Bitmap.createScaledBitmap(bm, MAX_BMP_WIDTH,
                (int) (hwRatio * MAX_BMP_WIDTH), true);
    }

    private float getBitmapScalingFactor(Bitmap bm) {
        int displayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int imageViewWidth = displayWidth;
        return ((float) imageViewWidth / (float) bm.getWidth());
    }

}