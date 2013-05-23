package com.themagpi.android;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.themagpi.api.Issue;

public class IssueGridAdapter extends BaseAdapter {
    

    private class ViewHolder {
        public ImageView imageView;
        public TextView  textView;
    }
    
    private ArrayList<Issue> mLocations;
    private LayoutInflater  mInflater;
    
    public IssueGridAdapter(Context context, ArrayList<Issue> locations) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLocations = locations;
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
        return position;
    }

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

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
        .cacheInMemory()
        .cacheOnDisc().resetViewBeforeLoading()
        .build();
        
        ImageLoader.getInstance().displayImage(locationModel.getCoverUrl(), viewHolder.imageView, options);
        viewHolder.textView.setText(locationModel.getTitle());
        
        return view;
    }

}
