package com.themagpi.adapters;

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
import com.themagpi.android.R;
import com.themagpi.api.Issue;

public class IssuesGridAdapter extends BaseAdapter {
    

    private class ViewHolder {
        public ImageView imageView;
        public TextView  textView;
    }
    
    private ArrayList<Issue> issues;
    private LayoutInflater  inflater;
    
    public IssuesGridAdapter(Context context, ArrayList<Issue> issues) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.issues = issues;
    }
    
    @Override
    public int getCount() {
        if (issues != null) {
            return issues.size();
        }
        
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (issues != null && position >= 0 && position < getCount()) {
            return issues.get(position);
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

        Issue issueModel = issues.get(position);
        
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_issue_item, parent, false);
            
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
        
        ImageLoader.getInstance().displayImage(issueModel.getCoverUrl(), viewHolder.imageView, options);
        viewHolder.textView.setText(issueModel.getTitle());
        
        return view;
    }

}
