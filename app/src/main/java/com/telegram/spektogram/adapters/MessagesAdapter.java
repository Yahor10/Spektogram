package com.telegram.spektogram.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.telegram.spektogram.R;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

/**
 * Created by alex-pers on 5/7/15.
 */
public class MessagesAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<TdApi.Message> messages;

    public MessagesAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        v = inflater.inflate(R.layout.list_item_chat_room, null);

        if (i % 2 <= 0) {
            v.setBackground(context.getResources().getDrawable(R.drawable.gradient_list_item_chat_room_blue));
        } else {
            v.setBackground(context.getResources().getDrawable(R.drawable.gradient_list_item_chat_room_red));
        }


        return v;
    }
}