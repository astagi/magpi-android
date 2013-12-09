package com.themagpi.fragments;

import java.io.File;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.themagpi.android.Config;
import com.themagpi.android.R;
import com.themagpi.api.Issue;
import com.themagpi.interfaces.Refreshable;
import com.themagpi.interfaces.RefreshableContainer;

public class IssueDetailsFragment extends SherlockFragment implements Refreshable {
    public final static String ARG_ISSUE = "IssueObject";
    private Issue issue;
    private DownloadManager dm;
    BroadcastReceiver downloadReceiver;
	private Menu menu;
    
    public void onCreate(Bundle si) {
        super.onCreate(si);
        dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void downloadIssue() {
        if(!this.canDisplayPdf(getActivity())) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.pdf_reader_required), Toast.LENGTH_LONG).show();
            return;
        }
        
        String file = issue.getId() + ".pdf";
        File pdf = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Config.ISSUE_FOLDER, file);
        
        if(pdf.exists() && !isDownloading(issue.getPdfUrl())) {
            Intent intentPdf = new Intent(Intent.ACTION_VIEW);
            intentPdf.setDataAndType(Uri.fromFile(pdf), "application/pdf");
            startActivity(intentPdf);
        } else if (!isDownloading(issue.getPdfUrl())) {
        	menu.findItem(R.id.menu_view).setVisible(false);
        	menu.findItem(R.id.menu_cancel_download).setVisible(true);
            Request request = new Request(Uri.parse(issue.getPdfUrl()));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(getActivity().getString(R.string.download_text) + " " + issue.getId());
            request.setDestinationInExternalPublicDir(Config.ISSUE_FOLDER, file);
            dm.enqueue(request);
        }
    }
    
    public void onResume() {
    	super.onResume();        
    	downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    Query query = new Query();
                    query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_URI);
                        String urlDownloaded = c.getString(columnIndex);
                	    if ((issue.getPdfUrl()+"/").equals(urlDownloaded)) {
                        	menu.findItem(R.id.menu_view).setVisible(true);
                        	menu.findItem(R.id.menu_cancel_download).setVisible(false);
                        } 
                    }
                    c.close();
                }
            }
        };
 
        getActivity().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	setHasOptionsMenu(true);
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
            case R.id.menu_cancel_download:
                cancelDownload();
                return true;
            case R.id.menu_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareText = String.format(getActivity().getString(R.string.share_text), issue.getId(), issue.getPdfUrl());
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_issue)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        
    }

    private void cancelDownload() {
    	Query query = new Query();
		query.setFilterByStatus(
			    DownloadManager.STATUS_PAUSED|
			    DownloadManager.STATUS_PENDING|
			    DownloadManager.STATUS_RUNNING);
		Cursor cur = dm.query(query);
		int col = cur.getColumnIndex(DownloadManager.COLUMN_URI);
		int colId = cur.getColumnIndex(DownloadManager.COLUMN_ID);
		for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			if(issue.getPdfUrl().equals(cur.getString(col)))
				dm.remove(cur.getLong(colId));
		}
		cur.close();
    	menu.findItem(R.id.menu_view).setVisible(true);
    	menu.findItem(R.id.menu_cancel_download).setVisible(false);
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
    
    private boolean isDownloading(String path) {
		boolean isDownloading = false;
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterByStatus(
		    DownloadManager.STATUS_PAUSED|
		    DownloadManager.STATUS_PENDING|
		    DownloadManager.STATUS_RUNNING);
		Cursor cur = dm.query(query);
		int col = cur.getColumnIndex(DownloadManager.COLUMN_URI);
		for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			isDownloading = path.equals(cur.getString(col));
			if(isDownloading)
				break;
		}
		cur.close();
		return isDownloading;
    }

    public void updateIssueView(final Issue issue) {
        this.issue = issue;
        TextView issueText = (TextView) getActivity().findViewById(R.id.article);
        issueText.setText(issue.getTitle() + " - " + issue.getDate());
        String htmlArticle = "<img align='left' src='%s' style='margin-right:10px; height:120px; width:90px;'/><b>%s</b>";
        WebView editorialText = (WebView) getSherlockActivity().findViewById(R.id.text_editorial);
        String content = issue.getEditorial().replace("\r\n", "<br/>").replace("\u00a0", " ");
        editorialText.loadData(String.format(htmlArticle, issue.getCoverUrl(), content), "text/html; charset=utf-8", "utf-8");
        editorialText.setVisibility(View.VISIBLE);
        getSherlockActivity().findViewById(R.id.web_content_progress).setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    
    public void onPause() {
        super.onPause(); 
        try {
        	getActivity().unregisterReceiver(downloadReceiver);
        } catch (IllegalArgumentException ex) {
        	ex.printStackTrace();
        }
    }

    @Override
    public void refresh() {
        ((RefreshableContainer) getActivity()).startRefreshIndicator(); 
        this.getSherlockActivity().findViewById(R.id.web_content_progress).setVisibility(View.VISIBLE);
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
    
    public void onPrepareOptionsMenu(Menu menu) {
    	this.menu = menu;
        Bundle args = getArguments();
        if (args != null) {
            issue = (Issue) args.getParcelable("IssueObject");
            if(isDownloading(issue.getPdfUrl())) {
            	menu.findItem(R.id.menu_view).setVisible(false);
            	menu.findItem(R.id.menu_cancel_download).setVisible(true);
            }
        }
        return;
    }

}