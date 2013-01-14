package com.themagpi.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;

public class HeadlinesFragment extends ListFragment {
    OnHeadlineSelectedListener mCallback;

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
        
        MagPiClient client = new MagPiClient();
        client.getIssues(new MagPiClient.OnIssuesReceivedListener() {
            public void onReceived(ArrayList<Issue> issues) {
                Log.e("ITEMS SIZE", "Downloaded issues: " + issues.size());
                
                String[] issuesHeadlines = new String[issues.size()];
                
                for(Issue issue : issues) {
                    Log.e("PDFURL", "URL:" + issue.getPdfUrl());
                }
                
                //showPdf(issues.get(issues.size() - 1));
                setListAdapter(new ArrayAdapter<Issue>(getActivity(), layout, issues));
                
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

    @Override
    public void onStart() {
        super.onStart();

        if (getFragmentManager().findFragmentById(R.id.issue_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onArticleSelected(position);
        getListView().setItemChecked(position, true);
    }
}