package com.themagpi.android;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import com.themagpi.api.Issue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class IssueGridAdapter extends BaseAdapter {
	

    private class ViewHolder {
        public ImageView imageView;
        public TextView  textView;
    }
    
    private ArrayList<Issue> mLocations;
    private Map<Integer, Drawable> drawables;
    private LayoutInflater  mInflater;
    
    public IssueGridAdapter(Context context, ArrayList<Issue> locations) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLocations = locations;
        drawables = Collections.synchronizedMap(new WeakHashMap<Integer, Drawable>());
    }
    
    @Override
    public int getCount() {
        if (mLocations != null) {
            return mLocations.size();
        }
        
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mLocations != null && position >= 0 && position < getCount()) {
            return mLocations.get(position);
        }
        
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mLocations != null && position >= 0 && position < getCount()) {
            return position;
        }
        
        return 0;
    }
    
    
    class RetreiveFeedTask extends AsyncTask<Void, Void, Drawable> {

        private Exception exception;
		private ViewHolder viewHolder;
		private Issue issue;
		private int position;
        
        public RetreiveFeedTask(Issue issue, ViewHolder viewHolder, int position) {
        	this.issue = issue;
        	this.viewHolder = viewHolder;
        	this.position = position;
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            try {
	            URL thumb_u = new URL(issue.getCoverUrl());
	            return Drawable.createFromStream(thumb_u.openStream(), "src");

            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(Drawable dr) {
        	/*viewHolder.imageView.destroyDrawingCache();
        	viewHolder.imageView.setImageDrawable(dr);
        	viewHolder.textView.setText(issue.getTitle());
            Log.e("PSOITION0", ":::" + position + ":::" + issue.getTitle());*/
            drawables.put(position, dr);
            viewHolder.imageView.setImageDrawable(dr);
        	//if(position < drawables.size() && drawables.get(position) == null)
        	//	drawables.add(position, dr);
        }

     };

     
    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        View view = convertView;
        ViewHolder viewHolder;

        Issue locationModel = mLocations.get(position);
        
        if (view == null) {
            view = mInflater.inflate(R.layout.item_issue_grid, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.grid_image);
            viewHolder.textView  = (TextView) view.findViewById(R.id.grid_label);
            
            view.setTag(viewHolder);
            

        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        


        
        if(drawables.get(position) == null)
        	new RetreiveFeedTask(locationModel, viewHolder, position).execute();
        if(drawables.get(position) != null) {
           	viewHolder.imageView.setImageDrawable(drawables.get(position));
        	Log.e("LOAD", "LOAd");

        }
        //if(position < drawables.size())

        //viewHolder.imageView.setImageURI(null);
        viewHolder.textView.setText(locationModel.getTitle());
        
        return view;
    }

}
