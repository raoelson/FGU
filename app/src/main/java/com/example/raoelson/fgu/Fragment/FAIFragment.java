package com.example.raoelson.fgu.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.example.raoelson.fgu.R;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;


/**
 * Created by Raoelson on 25/08/2017.
 */

public class FAIFragment extends Fragment implements View.OnClickListener{

    private Button mExpandButton;
    private Button mMoveChildButton;
    private Button mMoveChildButton2;
    private ExpandableRelativeLayout mExpandLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fai, container, false);
        mExpandButton = (Button) v.findViewById(R.id.expandButton);
        mMoveChildButton = (Button) v.findViewById(R.id.moveChildButton);
        mMoveChildButton2 = (Button) v.findViewById(R.id.moveChildButton2);
        mExpandLayout = (ExpandableRelativeLayout) v.findViewById(R.id.expandableLayout);
        mExpandButton.setOnClickListener(this);
        mMoveChildButton.setOnClickListener(this);
        mMoveChildButton2.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.expandButton:
                mExpandLayout.toggle();
                break;
            case R.id.moveChildButton:
                mExpandLayout.moveChild(0);
                break;
            case R.id.moveChildButton2:
                mExpandLayout.moveChild(1);
                break;
            case R.id.moveTopButton:
                mExpandLayout.move(0);
                break;
            case R.id.setCloseHeightButton:
                mExpandLayout.setClosePosition(mExpandLayout.getCurrentPosition());
                break;*/
        }
    }
}
