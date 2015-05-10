package com.telegram.spektogram.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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
 * Created by alex-pers on 4/29/15.
 */
public class ChatRoomsAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<TdApi.Chat> chats;

    public ChatRoomsAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
    }

    public ChatRoomsAdapter(LayoutInflater inflater, Context context, ArrayList<TdApi.Chat> chats) {
        this.inflater = inflater;
        this.context = context;
        this.chats = chats;
    }

    public ArrayList<TdApi.Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<TdApi.Chat> chats) {
        this.chats = chats;
    }

    public void addChat(TdApi.Chat chat) {

        chats.add(chat);
    }

    @Override
    public int getCount() {

        if (chats == null) {
            return 0;
        }
        return chats.size();
    }

    @Override
    public Object getItem(int i) {

        if (chats != null) {
            TdApi.Chat chat = chats.get(i);
            if (chat != null) {
                return chat;
            }
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (chats != null) {
            TdApi.Chat chat = chats.get(i);
            if (chat != null) {
                return chat.id;
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
            view = inflater.inflate(R.layout.list_item_chat_room, null);


            holder.background = (View) view
                    .findViewById(R.id.frame_chat_about);
            holder.user_photo = (ImageView) view
                    .findViewById(R.id.img_user_photo);
            holder.chat_name = (TextView) view.findViewById(R.id.txt_chat_name);
            holder.last_message = (TextView) view.findViewById(R.id.txt_last_message);
            holder.time_last_message = (TextView) view.findViewById(R.id.txt_chat_last_message_time);
            holder.not_saw_messages = (TextView) view.findViewById(R.id.txt_chat_not_saw_count_message);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.setData((TdApi.Chat) getItem(i));

        return view;
    }


   public  class ViewHolder {

       public TdApi.Chat chat;

        View background;
        ImageView user_photo;
        TextView chat_name;
        TextView last_message;
        TextView time_last_message;
        TextView not_saw_messages;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setData(TdApi.Chat chat) {

            this.chat = chat;

            if (Math.abs(chat.id % 2) == 0) {
                background.setBackground(context.getResources().getDrawable(R.drawable.gradient_list_item_chat_room_blue));
            } else {
                background.setBackground(context.getResources().getDrawable(R.drawable.gradient_list_item_chat_room_red));
            }

            if (chat.type instanceof TdApi.GroupChatInfo) {
                chat_name.setText(((TdApi.GroupChatInfo) chat.type).groupChat.title);
            }

            if (chat.topMessage.message instanceof TdApi.MessageText) {
                last_message.setText(((TdApi.MessageText) chat.topMessage.message).text);
            } else {
                last_message.setText("Медиаконтент");
            }

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            Date date = new Date();
            date.setTime(chat.topMessage.forwardDate);
            time_last_message.setText(df.format("hh:mm ", date));

            not_saw_messages.setVisibility(View.GONE);

        }
    }
}
