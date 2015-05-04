package com.telegram.spektogram.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.telegram.spektogram.R;

import java.util.ArrayList;

public class ContactsAdapter extends ArrayAdapter<Contact> {

    private final LayoutInflater mInflater;
    private final ArrayList<Contact> mContacts;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        mContacts = contacts;
        // Instantiates a new AlphabetIndexer bound to the column used to sort contact names.
        // The cursor is left null, because it has not yet been retrieved.
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item
        Contact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View view = convertView;

        ViewContactHolder contactHolder = null;
        ViewHeaderHolder headerHolder = null;

        int type = getItemViewType(position);
        switch (type) {
            case BaseContactItem.TYPE_ITEM:
                if (view == null) {
                    contactHolder = new ViewContactHolder();
                    view = mInflater.inflate(R.layout.adapter_contact_item, null);
                    contactHolder.tvName = (TextView) view.findViewById(R.id.tvName);
                    contactHolder.tvPhone = (TextView) view.findViewById(R.id.tvPhone);
                    view.setTag(contactHolder);
                } else {
                     contactHolder = (ViewContactHolder) view.getTag();
                }
                contactHolder.tvName.setText(contact.name);

                if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
                    final String number = contact.numbers.get(0).number;
                    contactHolder.tvPhone.setText(number);
                }
                break;
            case BaseContactItem.TYPE_SEPARATOR:
                if (view == null) {
                    headerHolder = new ViewHeaderHolder();
                    view = mInflater.inflate(R.layout.adapter_contact_header, null);
                    headerHolder.tvName = (TextView) view.findViewById(R.id.tvName);

                    view.setTag(headerHolder);
                }else{
                    headerHolder = (ViewHeaderHolder) view.getTag();
                }
                headerHolder.tvName.setText(contact.name);
                break;
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        final Contact item = getItem(position);
        return item.getType();
    }

    public static class ViewContactHolder {
        public TextView tvName;
        public TextView tvPhone;
    }

    public static class ViewHeaderHolder {
        public TextView tvName;
    }


}
