package com.telegram.spektogram.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.telegram.spektogram.R;


/**
 * Created by alex-pers on 4/29/15.
 */
public class ChatRoomsAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;

    public ChatRoomsAdapter(LayoutInflater inflater, Context context) {
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
