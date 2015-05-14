package com.telegram.spektogram.contacts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.activity.ContactsActivity;
import com.telegram.spektogram.enums.ContactType;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ychabatarou on 13.05.2015.
 */
public class TelegramContactsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView list;
    private ContactsAdapter adapterContacts;
    private Map<String, TdApi.User> userMap;


    public TelegramContactsFragment(Map<String, TdApi.User> userMap) {
        this.userMap = userMap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_contacts, null);
        list  = (ListView) inflate.findViewById(R.id.lvContacts);
        list.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setOnItemClickListener(this);
        loadTelegramContacts();
        return inflate;
    }


//
    private void loadTelegramContacts() {
        final FragmentActivity activity = getActivity();
        final ContactFetcher contactFetcher = new ContactFetcher(activity, userMap);

        List<Contact> actions = new ArrayList<Contact>(3);

        ArrayList<Contact> listContacts = contactFetcher.fetchTelegramContacts(actions);
        adapterContacts = new ContactsAdapter(activity, listContacts,list);
        list.setAdapter(adapterContacts);

    }

    public void loadTelegramContacts(Context context) {
        final ContactFetcher contactFetcher = new ContactFetcher(context, userMap);

        List<Contact> actions = new ArrayList<Contact>(3);

        ArrayList<Contact> listContacts = contactFetcher.fetchTelegramContacts(actions);
        adapterContacts = new ContactsAdapter(context, listContacts,list);
        list.setAdapter(adapterContacts);

    }

    public ListView getList() {
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ContactsAdapter adapter = (ContactsAdapter) getList().getAdapter();

        final Contact item = adapter.getItem(position);
        if (item.getType() == ContactType.Action && item.name.equals(getString(R.string.create_new_group))) {
            startActivity(ContactsActivity.buildStartIntent(getActivity(), true, true, false));
        }
        adapter.notifyDataSetChanged();
    }

}
