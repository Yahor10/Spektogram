package com.telegram.spektogram.pages;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.activity.MessagesActivity;
import com.telegram.spektogram.adapters.ChatRoomsAdapter;
import com.telegram.spektogram.application.ApplicationSpektogram;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by alex-pers on 4/29/15.
 */
public class ChatRoom extends Fragment implements Client.ResultHandler, DialogExitListener {
    ListView chatslistView;
    ChatRoomsAdapter adapter;
    TdApi.Chat longClickChat = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.page_chat_room, null);

        chatslistView = (ListView) view.findViewById(R.id.list_chats);
        chatslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), MessagesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                TdApi.Chat chat = ((ChatRoomsAdapter.ViewHolder) view.getTag()).chat;
                ApplicationSpektogram.chat = chat;
                getActivity().startActivity(intent);
            }
        });

        chatslistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TdApi.Chat chat = ((ChatRoomsAdapter.ViewHolder) view.getTag()).chat;
                longClickChat = chat;

                DeleteDialog deleteDialog = new DeleteDialog();
                deleteDialog.setListener(ChatRoom.this);
                deleteDialog.setMessage("Удалить историю сообщений");
                deleteDialog.show(getActivity().getFragmentManager(), "tag");
                return true;
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadAllChats();
    }

    // kostil'
    int id_users = 1;


    public void getMessagesByIdUsers(ArrayList<TdApi.Chat> chats) {

        for (TdApi.Chat chat : chats) {

            if (chat.type instanceof TdApi.PrivateChatInfo) {
                id_users = ((TdApi.PrivateChatInfo) chat.type).user.id;

                ApplicationSpektogram.getApplication(getActivity().getBaseContext()).sendFunction(new TdApi.GetChatHistory(chat.id, id_users, 0, 50), new Client.ResultHandler() {

                    @Override
                    public void onResult(TdApi.TLObject object) {

                    }
                });

            } else if (chat.type instanceof TdApi.GroupChatInfo) {
                ApplicationSpektogram.getApplication(getActivity().getBaseContext()).sendFunction(new TdApi.GetChatHistory(chat.id, id_users, 0, 50), new Client.ResultHandler() {

                    @Override
                    public void onResult(TdApi.TLObject object) {

                    }
                });
            }

        }

    }

    public void reloadAllChats() {
        ApplicationSpektogram.getApplication(getActivity().getBaseContext()).sendFunction(new TdApi.GetChats(0, 100), this);

    }

    @Override
    public void onResult(TdApi.TLObject object) {
        TdApi.Chats chats = (TdApi.Chats) object;

        final TdApi.Chat[] arr = chats.chats;
        final ArrayList<TdApi.Chat> chatArrayList = new ArrayList<TdApi.Chat>(Arrays.asList(arr));


        final Activity activity = getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new ChatRoomsAdapter(activity.getLayoutInflater(), activity, chatArrayList);
                chatslistView.setAdapter(adapter);
            }
        });
        getMessagesByIdUsers(chatArrayList);
    }

    private final BroadcastReceiver updateFileDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            ApplicationSpektogram.getApplication(getActivity().getBaseContext()).sendFunction(new TdApi.GetChats(0, 50), ChatRoom.this);

        }
    };


    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(updateFileDownloadReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_FILE_DOWNLOADED));
    }

    @Override
    public void onStop() {

        try {
            getActivity().unregisterReceiver(updateFileDownloadReceiver);
        } catch (Exception e) {
        }
        super.onStop();
    }

    @Override
    public void exitTest() {
        if (longClickChat != null) {
            ApplicationSpektogram.getApplication(getActivity().getBaseContext()).sendFunction(new TdApi.DeleteChatHistory(longClickChat.id), new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    reloadAllChats();
                }
            });

        }


    }
}
