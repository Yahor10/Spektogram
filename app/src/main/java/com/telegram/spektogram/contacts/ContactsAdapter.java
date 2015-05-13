package com.telegram.spektogram.contacts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.activity.ContactsActivity;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.application.Constants;
import com.telegram.spektogram.enums.ContactType;

import org.drinkless.td.libcore.telegram.TdApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ContactsAdapter extends ArrayAdapter<Contact> {

    private final LayoutInflater mInflater;
    private final ArrayList<Contact> mContacts;
    private final ListView lvContacts;
    private final boolean mNewGroup;
    private final boolean mNewMessage;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        mContacts = contacts;
        ContactsActivity activity = (ContactsActivity) context;
        lvContacts = activity.getLvContacts();
        final Intent intent = activity.getIntent();
        mNewGroup = intent.getBooleanExtra(ContactsActivity.EXTRA_NEW_GROUP, false);
        mNewMessage = intent.getBooleanExtra(ContactsActivity.EXTRA_NEW_MESSAGE, false);
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
        ViewContactTelegramHolder viewContactTelegramHolder = null;
        ViewActionHolder viewActionHolder = null;
        ContactType type = contact.getType();
        switch (type) {
            case Contanct:
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
            case Separator:
                if (view == null) {
                    headerHolder = new ViewHeaderHolder();
                    view = mInflater.inflate(R.layout.adapter_contact_header, null);
                    headerHolder.tvName = (TextView) view.findViewById(R.id.tvName);

                    view.setTag(headerHolder);
                } else {
                    headerHolder = (ViewHeaderHolder) view.getTag();
                }
                headerHolder.tvName.setText(contact.name);
                break;
            case TelegramContact:
                if (view == null) {
                    viewContactTelegramHolder = new ViewContactTelegramHolder();
                    view = mInflater.inflate(R.layout.adapter_contact_telegram, null);

                    viewContactTelegramHolder.tvName = (TextView) view.findViewById(R.id.tvName);
                    viewContactTelegramHolder.imageView= (ImageView) view.findViewById(R.id.imageContact);
                    viewContactTelegramHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);

                    viewContactTelegramHolder.tvDate = (TextView) view.findViewById(R.id.tvDate);
                    view.setTag(viewContactTelegramHolder);
                } else {
                    viewContactTelegramHolder = (ViewContactTelegramHolder) view.getTag();
                }

                viewContactTelegramHolder.tvName.setText(contact.name);

                if (mNewGroup) {
                    viewContactTelegramHolder.checkBox.setVisibility(View.VISIBLE);
                    final boolean itemChecked = lvContacts.isItemChecked(position);
                    viewContactTelegramHolder.checkBox.setChecked(itemChecked);
                } else if(mNewMessage){
                    viewContactTelegramHolder.checkBox.setVisibility(View.GONE);
                }else{
                    viewContactTelegramHolder.checkBox.setVisibility(View.GONE);
                }

                final TdApi.User user = contact.getUser();
                Log.v(Constants.LOG_TAG,"cache user" +user);
                final TdApi.File photoSmall = user.photoSmall;
                if(photoSmall instanceof TdApi.FileLocal) {
                    TdApi.FileLocal local = (TdApi.FileLocal) photoSmall;
                    Log.v(Constants.LOG_TAG,"cache local" +local);
                    final Bitmap bitmapFromMemCache = ApplicationSpektogram.getApplication(getContext()).getBitmapFromMemCache(local.path);
                    Log.v(Constants.LOG_TAG,"cache item" + local.path);
                    if(bitmapFromMemCache != null){
                        viewContactTelegramHolder.imageView.setImageBitmap(bitmapFromMemCache);
                    }else{
                        viewContactTelegramHolder.imageView.setImageResource(R.drawable.user_photo);
                    }
                }else if(photoSmall instanceof TdApi.FileEmpty){
                    viewContactTelegramHolder.imageView.setImageResource(R.drawable.user_photo);
                }

                final TdApi.UserStatus status = user.status;
                if (status instanceof TdApi.UserStatusOffline) {
                    TdApi.UserStatusOffline offline = (TdApi.UserStatusOffline) status;
                    String date = DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(offline.wasOnline));
                    viewContactTelegramHolder.tvDate.setText(date);
                }
                break;
            case Action:
                if (view == null) {
                    viewActionHolder = new ViewActionHolder();
                    view = mInflater.inflate(R.layout.adapter_contact_action, null);
                    viewActionHolder.tvName = (TextView) view.findViewById(R.id.tvName);

                    view.setTag(viewActionHolder);
                } else {
                    viewActionHolder = (ViewActionHolder) view.getTag();
                }
                viewActionHolder.tvName.setText(contact.name);
                break;
        }


        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        final Contact item = getItem(position);
        return item.getType().ordinal();
    }

    public static class ViewContactHolder {
        public TextView tvName;
        public TextView tvPhone;
    }

    public static class ViewContactTelegramHolder {
        public ImageView imageView;
        public TextView tvName;
        public CheckBox checkBox;
        public TextView tvDate;
    }

    public static class ViewHeaderHolder {
        public TextView tvName;
    }

    public static class ViewActionHolder {
        public TextView tvName;
    }

}
