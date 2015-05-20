package com.telegram.spektogram.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by alex-pers on 5/7/15.
 */
public class MessagesAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<TdApi.Message> messages;
    int id_owner_user;

    private static final int TYPE_ITEM_TO_ME = 0;
    private static final int TYPE_ITEM_FROM_ME = 1;

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

        sortMessages();
    }

    public void addMessageAndReplaceOldUserPhotoMessage(TdApi.Message message) {
        if (message != null) {

            if (id_owner_user == message.fromId && message.message instanceof TdApi.MessagePhoto) {
                TdApi.Photo photo = ((TdApi.MessagePhoto) message.message).photo;
                TdApi.FileLocal file_for_replase = null;

                for (int i = photo.photos.length - 1; i >= 0; i--) {
                    if (photo.photos[i].photo instanceof TdApi.FileLocal) {
                        file_for_replase = (TdApi.FileLocal) photo.photos[i].photo;
                        break;
                    }
                }
                if (file_for_replase != null) {
                    for (int i = 0; i < messages.size(); i++) {
                        if (messages.get(i).fromId == id_owner_user && messages.get(i).message instanceof TdApi.MessagePhoto) {
                            TdApi.Photo photo1 = ((TdApi.MessagePhoto) message.message).photo;
                            for (TdApi.PhotoSize p_s : photo1.photos) {
                                if (p_s.photo instanceof TdApi.FileLocal) {
                                    if (((TdApi.FileLocal) p_s.photo).id == file_for_replase.id) {
                                        messages.set(i, message);
                                        return;
                                    }
                                } else if (p_s.photo instanceof TdApi.FileEmpty) {
                                    if (((TdApi.FileEmpty) p_s.photo).id == file_for_replase.id) {
                                        messages.set(i, message);
                                        return;
                                    }
                                }
                            }

                        }
                    }
                }

            }

            this.messages.add(message);
            sortMessages();
        }
    }

    public void sortMessages() {
        Collections.sort(this.messages, new Comparator<TdApi.Message>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(TdApi.Message s1, TdApi.Message s2) {
                return Integer.compare(s1.date, s2.date);
            }
        });
    }

    public void addMessage(TdApi.Message message) {
        if (message != null) {

            this.messages.add(message);
            sortMessages();

        }
    }


    public void addMessages(ArrayList<TdApi.Message> messages) {
        this.messages.addAll(messages);
        Collections.sort(this.messages, new Comparator<TdApi.Message>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(TdApi.Message s1, TdApi.Message s2) {
                return Integer.compare(s1.date, s2.date);
            }
        });
    }

    public TdApi.Message findMessageWithFileId(int file_id) {

        for (TdApi.Message mess : messages) {
            if (mess.message instanceof TdApi.MessagePhoto) {
                for (TdApi.PhotoSize p : ((TdApi.MessagePhoto) mess.message).photo.photos) {
                    if (p.photo instanceof TdApi.FileEmpty) {
                        if (((TdApi.FileEmpty) p.photo).id == file_id) {
                            return mess;
                        }
                    } else if (p.photo instanceof TdApi.FileLocal) {
                        if (((TdApi.FileLocal) p.photo).id == file_id) {
                            return mess;
                        }
                    }

                }

            }
        }

        return null;
    }

    public void replaceMessage(TdApi.Message newMessage) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id == newMessage.id) {
                messages.set(i, newMessage);
            }
        }
    }

    public void removeMessageById(int messgage_id) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id == messgage_id) {
                messages.remove(i);
            }
        }
    }

    public int getId_owner_user() {
        return id_owner_user;
    }

    public void setId_owner_user(int id_owner_user) {
        this.id_owner_user = id_owner_user;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).fromId != id_owner_user) {
            return TYPE_ITEM_TO_ME;
        } else {
            return TYPE_ITEM_FROM_ME;
        }

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

        ViewHolder holder = null;

        int type = getItemViewType(i);

        if (view == null) {

            switch (type) {
                case TYPE_ITEM_FROM_ME:
                    view = inflater.inflate(R.layout.list_item_message_type_text_from_me, null);
                    break;
                case TYPE_ITEM_TO_ME:
                    view = inflater.inflate(R.layout.list_item_message_type_text_to_me, null);
                    break;
            }

            holder = new ViewHolder();
            holder.user_photo = (ImageView) view
                    .findViewById(R.id.img_user_photo);
            holder.txt_message = (TextView) view.findViewById(R.id.txt_message);
            holder.time_message = (TextView) view.findViewById(R.id.txt_message_time);
            holder.img_photo_message = (ImageView) view.findViewById(R.id.img_message_photo);

            view.setTag(R.id.TAG_HOLDER_VIEW, holder);
            view.setTag(((TdApi.Message) getItem(i)).id);

        } else {
            holder = (ViewHolder) view.getTag(R.id.TAG_HOLDER_VIEW);
        }

        holder.setData((TdApi.Message) getItem(i));


        return view;
    }

    public class ViewHolder {

        public TdApi.Message message;

        ImageView user_photo;

        TextView txt_message;
        TextView time_message;
        ImageView img_photo_message;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setData(TdApi.Message message) {

            this.message = message;

            if (message != null) {
                if (message.message instanceof TdApi.MessageText) {
                    txt_message.setText(((TdApi.MessageText) message.message).text);

                    img_photo_message.setVisibility(View.GONE);
                    txt_message.setVisibility(View.VISIBLE);

                } else if (message.message instanceof TdApi.MessagePhoto) {

                    img_photo_message.setVisibility(View.VISIBLE);
                    txt_message.setVisibility(View.GONE);
                    int id_file = 0;

                    if (((TdApi.MessagePhoto) message.message).photo.photos.length != 0) {
                        int lenght = ((TdApi.MessagePhoto) message.message).photo.photos.length;

                        boolean flag_file_is_local = false;

                        for (int i = lenght - 1; i >= 0; i--) {
                            if (((TdApi.MessagePhoto) message.message).photo.photos[i].photo instanceof TdApi.FileLocal) {
                                String url = ((TdApi.FileLocal) ((TdApi.MessagePhoto) message.message).photo.photos[i].photo).path;


                                final Bitmap bitmapFromMemCache = ApplicationSpektogram.getApplication(context).getBitmapFromMemCache(url);
                                if (bitmapFromMemCache != null) {
                                    img_photo_message.setImageBitmap(bitmapFromMemCache);
                                } else {
                                    final Bitmap bitmap = BitmapFactory.decodeFile(url);
                                    final ApplicationSpektogram application = ApplicationSpektogram.getApplication(context);
                                    application.addBitmapToMemoryCache(url, bitmap);
                                    img_photo_message.setImageBitmap(bitmap);
                                }

                                flag_file_is_local = true;
                                break;
                            }
                        }

                        if (!flag_file_is_local) {
                            if (lenght == 1) {
                                lenght = 0;
                            } else if (lenght > 1) {
                                lenght = lenght / 2;
                            }

                            if (((TdApi.MessagePhoto) message.message).photo.photos[lenght].photo instanceof TdApi.FileEmpty) {
                                img_photo_message.setImageResource(R.drawable.user_photo);
                                id_file = ((TdApi.FileEmpty) ((TdApi.MessagePhoto) message.message).photo.photos[lenght].photo).id;
                                ApplicationSpektogram.getApplication(context).sendFunction(new TdApi.DownloadFile(id_file), new Client.ResultHandler() {
                                    @Override
                                    public void onResult(TdApi.TLObject object) {

                                    }
                                });

                            }
                        }

                    }

                } else {
                    img_photo_message.setVisibility(View.GONE);
                    txt_message.setVisibility(View.VISIBLE);

                    txt_message.setText("Не текстовое сообщение");
                }
                String date = DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(message.date));

                time_message.setText(date);

            }
        }
    }

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");


}