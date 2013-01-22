package com.themagpi.android;

import java.io.File;
import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;


public class IssueFragment extends SherlockFragment {
    final static String ARG_ISSUE = "IssueObject";
    private final int MAX_BMP_WIDTH = 600;
    int mCurrentPosition = -1;
    MagPiClient client = new MagPiClient();
    ProgressDialog progressBar;
    Issue issue;
    
    class DownloadFileBroadcastReceiver extends BroadcastReceiver {
        private Handler updateUI = new Handler();

        @Override
        public void onReceive(Context ctxt, final Intent intent) {
            updateUI.post(new Runnable() {
                
                @Override
                public void run() {
                    
                    if(!intent.hasExtra("status"))
                        return;
                    
                    switch(intent.getIntExtra("status", DownloadFileService.STOP)) {
                        case DownloadFileService.COMPLETE:
                            progressBar.dismiss();
                            Log.e("DOWNLOADSERVICE", "COMPLETE");
                            break;
                        case DownloadFileService.UPDATE:
                            progressBar.setProgress(intent.getExtras().getInt("percentage"));
                            Log.e("DOWNLOADSERVICE", "" + intent.getExtras().getInt("percentage") + "%");
                            break;
                    }
                }
                
            });
        }
    };
    
    private DownloadFileBroadcastReceiver receiver;
    
    public void onCreate(Bundle si) {
        super.onCreate(si);
        this.setHasOptionsMenu(true);

    }
    
    @SuppressWarnings("deprecation")
    public void downloadIssue() {
        progressBar = new ProgressDialog(this.getActivity());
        progressBar.setCancelable(false);
        progressBar.setMessage("Downloading " + issue.getTitle());
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setButton("Cancel", new ProgressDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
                getActivity().stopService(new Intent(getActivity(), DownloadFileService.class));
            }
            
        });
        progressBar.setMax(100);
        progressBar.show();
        
        Intent service = new Intent(getActivity(), DownloadFileService.class);
        if(issue != null)
            service.putExtra("IssueObject", issue);
        this.getActivity().startService(service);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            //mCurrentPosition = savedInstanceState.getInt(ARG_ISSUE);
        }
        return inflater.inflate(R.layout.article_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadFileService.BROADCAST_STATUS);
        
        receiver = new DownloadFileBroadcastReceiver();
        
        getActivity().registerReceiver(receiver, filter);

        Bundle args = getArguments();
        if (args != null) {
            issue = (Issue)args.getParcelable("IssueObject");
            updateIssueView(issue);
        } 
        
        //downloadIssue();
        
        /*else if (mCurrentPosition != -1) {
            updateIssueView(mCurrentPosition);
        }*/
    }
    

    public void updateIssueView(Issue issue) {
        TextView issueText = (TextView) getActivity().findViewById(R.id.article);
        issueText.setText(issue.getTitle() + " - " + issue.getDate());
        showCover(issue);
        //mCurrentPosition = issue;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(ARG_POSITION, mCurrentPosition);
    }
    
    Bitmap ScaleBitmap(Bitmap bm, float scalingFactor) {
        int scaleHeight = (int) (bm.getHeight() * scalingFactor);
        int scaleWidth = (int) (bm.getWidth() * scalingFactor);

        if(scaleWidth <= MAX_BMP_WIDTH)
            return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
        float hwRatio = ((float)bm.getHeight() / bm.getWidth());
        return Bitmap.createScaledBitmap(bm, MAX_BMP_WIDTH, (int)(hwRatio * MAX_BMP_WIDTH), true);
    }
    
    private float getBitmapScalingFactor(Bitmap bm) {
        int displayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int imageViewWidth = displayWidth;
        return ( (float) imageViewWidth / (float) bm.getWidth() );
    }
    
    private void showCover(final Issue issue) {
        
        client.getCover(issue, new MagPiClient.OnFileReceivedListener() {
            public void onReceived(byte[] data) {
                Log.e("File Status", "Arrived");

                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File (sdCard.getAbsolutePath() + "/MagPi/" + issue.getId());
                    dir.mkdirs();
                    File file = new File(dir, "cover.jpg");

                    FileOutputStream f = new FileOutputStream(file);
                    f.write(data);
                    f.flush();
                    f.close();
                    
                    Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
                    ImageView image=(ImageView)IssueFragment.this.getActivity().findViewById(R.id.cover);
                    image.setImageBitmap(ScaleBitmap(bmp, getBitmapScalingFactor(bmp)));
                        
                } catch (Exception e) {
                    Log.e("error", "Error opening file.", e);
                }
            }
        });
    }
    
    private void showPdf(final Issue issue) {
        client.getPdf(issue, new MagPiClient.OnFileReceivedListener() {
            public void onReceived(byte[] data) {
                Log.e("File Status", "Arrived");

                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File (sdCard.getAbsolutePath() + "/MagPi/" + issue.getId());
                    dir.mkdirs();
                    File file = new File(dir, issue.getId() + ".pdf");

                    FileOutputStream f = new FileOutputStream(file);
                    f.write(data);
                    f.flush();
                    f.close();
                                        
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("error", "Error opening file.", e);
                }
            }
        });
    }
    
    public void onPause() {
        super.onPause();
        if(getActivity() != null && client != null)
            client.close(getActivity());
        if(getActivity() != null)
            getActivity().unregisterReceiver(receiver);
    }

}