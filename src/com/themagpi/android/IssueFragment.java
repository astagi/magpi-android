package com.themagpi.android;

import java.io.File;
import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.themagpi.api.Issue;
import com.themagpi.api.MagPiClient;

public class IssueFragment extends SherlockFragment implements Refreshable {
    final static String ARG_ISSUE = "IssueObject";
    private final int MAX_BMP_WIDTH = 600;
    private int mCurrentPosition = -1;
    private MagPiClient client = new MagPiClient();
    private ProgressDialog progressBar;
    private Issue issue;
    

    class DownloadFileBroadcastReceiver extends BroadcastReceiver {
        private Handler updateUI = new Handler();

        @Override
        public void onReceive(Context ctxt, final Intent intent) {
            updateUI.post(new Runnable() {

                @Override
                public void run() {

                    if (!intent.hasExtra("status"))
                        return;

                    switch (intent.getIntExtra("status",
                            DownloadFileService.ERROR)) {
                    case DownloadFileService.COMPLETE:
                        progressBar.dismiss();
                        File file = (File) intent.getExtras().getSerializable("file");
                        if (file != null) {
                            Intent intentPdf = new Intent(Intent.ACTION_VIEW);
                            intentPdf.setDataAndType(Uri.fromFile(file), "application/pdf");
                            startActivity(intentPdf);
                        }
                        Log.e("DOWNLOADSERVICE", "COMPLETE");
                        break;
                    case DownloadFileService.UPDATE:
                        progressBar.setProgress(intent.getExtras().getInt("percentage"));
                        Log.e("DOWNLOADSERVICE", ""
                                + intent.getExtras().getInt("percentage") + "%");
                        break;
                    case DownloadFileService.ERROR:
                        if(progressBar != null && progressBar.isShowing())
                            progressBar.dismiss();
                        Toast.makeText(getActivity(), "Error downloading Issue", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

            });
        }
    };

    private DownloadFileBroadcastReceiver receiver;
    private Menu menu;
    private LayoutInflater inflater;

    public void onCreate(Bundle si) {
        super.onCreate(si);
        this.setHasOptionsMenu(true);
    }

    @SuppressWarnings("deprecation")
    public void downloadIssue() {
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
                    getActivity().stopService(new Intent(getActivity(), DownloadFileService.class));
                }
    
            });
            progressBar.setMax(100);
            progressBar.show();
    
            Intent service = new Intent(getActivity(), DownloadFileService.class);
            if (issue != null)
                service.putExtra("IssueObject", issue);
            
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getSherlockActivity());
            
            service.putExtra("keep", prefs.getBoolean("pref_store_issue", true));
            
            this.getActivity().startService(service);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            // mCurrentPosition = savedInstanceState.getInt(ARG_ISSUE);
        }
        return inflater.inflate(R.layout.article_view, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { 
        inflater.inflate(R.menu.issue, menu);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        this.inflater = (LayoutInflater) ((SherlockFragmentActivity) getActivity()).getSupportActionBar().getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadFileService.BROADCAST_STATUS);

        receiver = new DownloadFileBroadcastReceiver();

        getActivity().registerReceiver(receiver, filter);

        Bundle args = getArguments();
        if (args != null) {
            issue = (Issue) args.getParcelable("IssueObject");
            updateIssueView(issue);
        }

        /*
         * else if (mCurrentPosition != -1) { updateIssueView(mCurrentPosition);
         * }
         */
    }

    public void updateIssueView(Issue issue) {
        this.issue = issue;
        TextView issueText = (TextView) getActivity().findViewById(R.id.article);
        issueText.setText(issue.getTitle() + " - " + issue.getDate());
        showCover();
        // mCurrentPosition = issue;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putInt(ARG_POSITION, mCurrentPosition);
    }
    
    public void onPause() {
        super.onPause(); 
        if (getActivity() != null && client != null)
            client.close(getActivity());
        if (getActivity() != null) {
            try {
                getActivity().unregisterReceiver(receiver);
            } catch(IllegalArgumentException e) {}
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

    private void showCover() {

        client.getCover(issue, new MagPiClient.OnFileReceivedListener() {
            public void onReceived(byte[] data) {
                Log.e("File Status", "Arrived");

                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/MagPi/"
                            + issue.getId());
                    dir.mkdirs();
                    File file = new File(dir, "cover.jpg");

                    FileOutputStream f = new FileOutputStream(file);
                    f.write(data);
                    f.flush();
                    f.close();

                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ImageView image = (ImageView) IssueFragment.this.getActivity().findViewById(R.id.cover);
                    image.setImageBitmap(ScaleBitmap(bmp, getBitmapScalingFactor(bmp)));

                } catch (Exception e) {
                    Log.e("error", "Error opening file.", e);
                }
            }
        });
    }

    @Override
    public void refresh() {
        if(menu != null)
            menu.findItem(R.id.menu_refresh).setActionView(inflater.inflate(R.layout.actionbar_refresh_progress, null));
        
    }

}