package com.telegram.spektogram.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.telegram.spektogram.R;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alex-pers on 5/7/15.
 */
public class MessagesAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<TdApi.Message> messages;
    int id_owner_user;

    public MessagesAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
        messages = new ArrayList<TdApi.Message>();
        id_owner_user = 0;
    }

    public MessagesAdapter(LayoutInflater inflater, Context context, ArrayList<TdApi.Message> messages, int user_owner_id) {
        this.inflater = inflater;
        this.context = context;
        this.messages = messages;
        this.id_owner_user = user_owner_id;
    }

    public ArrayList<TdApi.Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<TdApi.Message> messages) {
        this.messages = messages;
    }

    public void addMessages(ArrayList<TdApi.Messages> messageses) {
        this.messages.addAll(messages);
    }

    public int getId_owner_user() {
        return id_owner_user;
    }

    public void setId_owner_user(int id_owner_user) {
        this.id_owner_user = id_owner_user;
    }

    @Override
    public int getCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        if (messages != null) {
            TdApi.Message message = messages.get(i);
            if (message != null) {

                return message;
            }
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (messages != null) {
            TdApi.Message message = messages.get(i);
            if (message != null) {
                return message.id;
            }
        }
        return i;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();
            if (messages.get(i).fromId == id_owner_user) {
                view = inflater.inflate(R.layout.list_item_message_type_text_from_me, null);
            } else {
                view = inflater.inflate(R.layout.list_item_message_type_text_to_me, null);
            }


            holder.user_photo = (ImageView) view
                    .findViewById(R.id.img_user_photo);
            holder.txt_message = (TextView) view.findViewById(R.id.txt_message);
            holder.time_message = (TextView) view.findViewById(R.id.txt_message_time);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.setData((TdApi.Message) getItem(i));


        return view;
    }

    public class ViewHolder {

        public TdApi.Message message;

        ImageView user_photo;

        TextView txt_message;
        TextView time_message;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setData(TdApi.Message message) {

            this.message = message;

            if (message != null) {
                if (message.message instanceof TdApi.MessageText) {
                    txt_message.setText(((TdApi.MessageText) message.message).text);
                } else {
                    txt_message.setText("Вам переслали файл.");
                }
                DateFormat df = new android.text.format.DateFormat();
                Date date = new Date();
                date.setTime(message.date);

                time_message.setText(df.format("hh:mm ", date));

            }
        }
    }
}