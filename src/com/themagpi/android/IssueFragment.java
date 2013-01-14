package com.themagpi.android;

import java.io.File;
import java.io.FileOutputStream;

import com.actionbarsherlock.app.SherlockFragment;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;

import com.actionbarsherlock.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;


public class IssueFragment extends SherlockFragment {
    final static String ARG_ISSUE = "IssueObject";
    int mCurrentPosition = -1;
    
    public void onCreate(Bundle si) {
        super.onCreate(si);
        this.setHasOptionsMenu(true);
        ActionBar actionBar = ((SherlockFragmentActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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

        Bundle args = getArguments();
        if (args != null) {
            updateIssueView((Issue)args.getParcelable("IssueObject"));
        } 
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

        return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
    }
    
    private float getBitmapScalingFactor(Bitmap bm) {
        int displayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();

        ImageView imageView=(ImageView)IssueFragment.this.getActivity().findViewById(R.id.cover);

        int imageViewWidth = displayWidth;

        return ( (float) imageViewWidth / (float) bm.getWidth() );
    }
    
    private void showCover(final Issue issue) {
        MagPiClient client = new MagPiClient();
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
        MagPiClient client = new MagPiClient();
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

}