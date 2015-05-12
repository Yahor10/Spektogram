package com.telegram.spektogram.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.telegram.spektogram.R;
import com.telegram.spektogram.views.HorizontalListView;

public class SelectBackgroundActivity extends ActionBarActivity {

    public static Intent buildStartIntent(Context context){
        return new Intent(context,SelectBackgroundActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_background);

        HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);
        listview.setAdapter(mAdapter);
    }


    private static String[] dataObjects = new String[]{ "Text #1",
            "Text #2",
            "Text #3",
            "Text #3","Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #3",
            "Text #2",};

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return dataObjects.length;
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_background, null);
            }else{
            }

            return convertView;
        }


    };

}
