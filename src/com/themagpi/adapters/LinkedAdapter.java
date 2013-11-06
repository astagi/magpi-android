package com.themagpi.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class LinkedAdapter extends SimpleAdapter {
    
    private int[] textViews;

    public LinkedAdapter(Context context, ArrayList<HashMap<String, Object>> list, int resource, String[] from, int[] to, int[] textViews) {
        super(context, list, resource, from, to);
        this.textViews = textViews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView noteView = null;
        for(int textViewId : textViews) {
            noteView = (TextView) view.findViewById(textViewId);
            if(noteView != null) {
                noteView.setAutoLinkMask(Linkify.ALL);
                noteView.setLinksClickable(true);
                noteView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
        return view;
    }
    

}