package com.telegram.spektogram.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.telegram.spektogram.R;
import com.telegram.spektogram.adapters.ChatRoomsAdapter;


/**
 * Created by alex-pers on 4/29/15.
 */
public class ChatRoom extends Fragment {
    ListView chatslistView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.page_chat_room, null);

        chatslistView = (ListView) view.findViewById(R.id.list_chats);
        ChatRoomsAdapter adapter = new ChatRoomsAdapter(getActivity().getLayoutInflater(), getActivity().getApplicationContext());
        chatslistView.setAdapter(adapter);
        chatslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "" + i, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
