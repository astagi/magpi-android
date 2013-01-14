package com.themagpi.android;

import com.themagpi.api.Issue;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class IssueFragment extends Fragment {
    final static String ARG_ISSUE = "IssueObject";
    int mCurrentPosition = -1;

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
        issueText.setText(issue.getTitle());
        //mCurrentPosition = issue;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(ARG_POSITION, mCurrentPosition);
    }
}