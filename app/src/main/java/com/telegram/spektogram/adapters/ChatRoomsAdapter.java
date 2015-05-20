package com.telegram.spektogram.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * Created by alex-pers on 4/29/15.
 */
public class ChatRoomsAdapter extends ArrayAdapter<TdApi.Chat> {
    LayoutInflater inflater;
    Context context;
    ArrayList<TdApi.Chat> chats;
    ArrayList<Integer> colors = new ArrayList<Integer>();

    public ChatRoomsAdapter(LayoutInflater inflater, Context context, ArrayList<TdApi.Chat> chats) {
        super(context, 0, chats);
        this.inflater = inflater;
        this.context = context;
        this.chats = chats;
        Collections.sort(this.chats, new Comparator<TdApi.Chat>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(TdApi.Chat s1, TdApi.Chat s2) {
                return Integer.compare(s2.topMessage.date, s1.topMessage.date);
            }
        });

    }

    public ArrayList<TdApi.Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<TdApi.Chat> chats) {
        this.chats = chats;

        Collections.sort(this.chats, new Comparator<TdApi.Chat>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(TdApi.Chat s1, TdApi.Chat s2) {
                return Integer.compare(s2.topMessage.date, s1.topMessage.date);
            }
        });
    }

    public void addChat(TdApi.Chat chat) {

        chats.add(chat);
        Collections.sort(this.chats, new Comparator<TdApi.Chat>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(TdApi.Chat s1, TdApi.Chat s2) {
                return Integer.compare(s2.topMessage.date, s1.topMessage.date);
            }
        });
    }

    @Override
    public int getCount() {

        if (chats == null) {
            return 0;
        }
        return chats.size();
    }

    @Override
    public TdApi.Chat getItem(int i) {
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

        final TdApi.Chat item = getItem(i);
        holder.setData((TdApi.Chat) item);

        int color_id = getRandomBackground(context);


        holder.background.setBackground(context.getResources().getDrawable(color_id));

        boolean flag_color_verify = true;
        int previous_color = 0;

        if ((i - 1) >= 0) {
            previous_color = colors.get(i - 1);
        }

        int next_color = 0;
        if (colors.size() >= (i + 1)) {
            next_color = colors.get(i + 1);
        }

        while (flag_color_verify) {
            if (previous_color == color_id || next_color == color_id) {
                color_id = getRandomBackground(context);
                holder.background.setBackground(context.getResources().getDrawable(color_id));
            } else {
                flag_color_verify = false;
            }
        }


        colors.add(i, color_id);

//

        return view;
    }


    public class ViewHolder {

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
            Random random = new Random();


            if (chat.type instanceof TdApi.GroupChatInfo) {
                user_photo.setImageResource(R.mipmap.ic_launcher);
                chat_name.setText(((TdApi.GroupChatInfo) chat.type).groupChat.title);
            } else if (chat.type instanceof TdApi.PrivateChatInfo) {


                chat_name.setText(((TdApi.PrivateChatInfo) chat.type).user.firstName);


                if (((TdApi.PrivateChatInfo) chat.type).user.photoSmall instanceof TdApi.FileLocal) {

                    String url = ((TdApi.FileLocal) ((TdApi.PrivateChatInfo) chat.type).user.photoSmall).path;

                    final Bitmap bitmapFromMemCache = ApplicationSpektogram.getApplication(context).getBitmapFromMemCache(url);
                    if (bitmapFromMemCache != null) {
                        user_photo.setImageBitmap(bitmapFromMemCache);
                    } else {
                        final Bitmap bitmap = BitmapFactory.decodeFile(url);
                        final ApplicationSpektogram application = ApplicationSpektogram.getApplication(context);
                        application.addBitmapToMemoryCache(url, bitmap);
                        user_photo.setImageBitmap(bitmap);
                    }

                } else if (((TdApi.PrivateChatInfo) chat.type).user.photoSmall instanceof TdApi.FileEmpty) {

                    user_photo.setImageResource(R.mipmap.ic_launcher);
                    int id_file = 0;
                    id_file = ((TdApi.FileEmpty) ((TdApi.PrivateChatInfo) chat.type).user.photoSmall).id;

                    MyClientHandlerUser myClientHandlerUser = new MyClientHandlerUser();
                    myClientHandlerUser.user = ((TdApi.PrivateChatInfo) chat.type).user;
                    myClientHandlerUser.userPhoto = user_photo;


                    ApplicationSpektogram.getApplication(context).sendFunction(new TdApi.DownloadFile(id_file), myClientHandlerUser);
                }


            }

            if (chat.topMessage.message instanceof TdApi.MessageText) {
                last_message.setText(((TdApi.MessageText) chat.topMessage.message).text);
            } else {
                last_message.setText("Медиаконтент");
            }

            android.text.format.DateFormat df = new android.text.format.DateFormat();

            String date = DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(chat.topMessage.date));
//
            time_last_message.setText(date);

            if (chat.unreadCount > 0) {
                not_saw_messages.setVisibility(View.VISIBLE);
                not_saw_messages.setText("" + chat.unreadCount);
            } else {
                not_saw_messages.setVisibility(View.GONE);
            }
        }
    }

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");

    public int getRandomBackground(Context context) {

        Random random = new Random();

        int r = random.nextInt(8);

        switch (r) {
            case 1:
                return R.drawable.gradient_blue_blue;
            case 2:
                return R.drawable.gradient_blue_to_blue;
            case 3:
                return R.drawable.gradient_blue_violet;
            case 4:
                return R.drawable.gradient_red_red;
            case 5:
                return R.drawable.gradient_red_yellow;
            case 6:
                return R.drawable.gradient_yellow_green;
            case 7:
                return R.drawable.gradient_yellow_to_red;

        }

        return R.drawable.gradient_list_item_chat_room_red;

    }

    public class MyClientHandlerUser implements Client.ResultHandler {
        public TdApi.User user = null;
        ImageView userPhoto = null;

        @Override
        public void onResult(TdApi.TLObject object) {
            MyClientHandlerView myClientHandlerView = new MyClientHandlerView();
            myClientHandlerView.userPhoto = userPhoto;

            ApplicationSpektogram.getApplication(context).sendFunction(new TdApi.GetUser(user.id), myClientHandlerView);
        }
    }


    public class MyClientHandlerView implements Client.ResultHandler {
        ImageView userPhoto = null;

        @Override
        public void onResult(TdApi.TLObject object) {
            if (userPhoto != null) {
                String url = ((TdApi.FileLocal) ((TdApi.User) object).photoSmall).path;
                final Bitmap bitmap = BitmapFactory.decodeFile(url);
                final ApplicationSpektogram application = ApplicationSpektogram.getApplication(context);
                application.addBitmapToMemoryCache(url, bitmap);
                userPhoto.setImageBitmap(bitmap);
            }
        }
    }
}
