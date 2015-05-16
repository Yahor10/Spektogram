package com.telegram.spektogram.pages;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
public class ChatRoom extends Fragment implements Client.ResultHandler {
    ListView chatslistView;
    ChatRoomsAdapter adapter;

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

        ApplicationSpektogram.getApplication(getActivity().getBaseContext()).sendFunction(new TdApi.GetChats(0, 30), this);


        return view;
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
}
