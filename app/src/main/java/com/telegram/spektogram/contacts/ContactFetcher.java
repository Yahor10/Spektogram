package com.telegram.spektogram.contacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.content.CursorLoader;

import com.telegram.spektogram.enums.ContactType;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

// new ContactFetcher(this).fetchAll();
public class ContactFetcher {
    private Context context;
    private final HashSet<String> azIndexer = new HashSet<String>();
    private final Map<String, TdApi.User> userMap;

    public ContactFetcher(Context c) {
        this.context = c;
        this.userMap = null;
    }

    public ContactFetcher(Context contactsActivity, Map<String, TdApi.User> userMap) {
        this.context = contactsActivity;
        this.userMap = userMap;
    }

    public ArrayList<Contact> fetchAll(List<Contact> actions) {
        ArrayList<Contact> listContacts = new ArrayList<Contact>();
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 ";
        CursorLoader cursorLoader = new CursorLoader(context, ContactsContract.Contacts.CONTENT_URI,
                null, // the columns to retrieve (all)
                selection, // the selection criteria (none)
                null, // the selection args (none)
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" // the sort order (default)
        );



        final List<Contact> telegramContacts = new ArrayList<Contact>();
        final List<Contact> userContacts = new ArrayList<Contact>();

        Cursor c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do {
                loadContactData(c,userContacts,telegramContacts);
            } while (c.moveToNext());
        }
        c.close();


        listContacts.addAll(actions);
        listContacts.addAll(telegramContacts);
        listContacts.addAll(userContacts);
        return listContacts;
    }

    public ArrayList<Contact> fetchTelegramContacts(List<Contact> actions) {
        ArrayList<Contact> listContacts = new ArrayList<Contact>();
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 ";
        CursorLoader cursorLoader = new CursorLoader(context, ContactsContract.Contacts.CONTENT_URI,
                null, // the columns to retrieve (all)
                selection, // the selection criteria (none)
                null, // the selection args (none)
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" // the sort order (default)
        );




        final List<Contact> telegramContacts = new ArrayList<Contact>();

        Cursor c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do {
                loadTelegramContacts(c, telegramContacts);
            } while (c.moveToNext());
        }
        c.close();

        listContacts.addAll(actions);
        listContacts.addAll(telegramContacts);
        return listContacts;
    }

    private void loadContactData(Cursor c, List<Contact> userContacts, List<Contact> telegramContacts) {
        // Get Contact ID
        int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
        String contactId = c.getString(idIndex);
        // Get Contact Name
        int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String contactDisplayName = c.getString(nameIndex);

        ContactType type;
        Contact header = null;
        final String substring = contactDisplayName.substring(0, 1);
        if (!azIndexer.contains(substring)) {
            azIndexer.add(substring);
            type = ContactType.Separator;
            header = new Contact(contactId, substring, type);
        }

        type = ContactType.Contanct;
        Contact contact = new Contact(contactId, contactDisplayName, type);

        fetchContactNumbers(c, contact);
        fetchContactEmails(c, contact);

        if(contact.getUser() != null) {
            telegramContacts.add(contact);
        }else{
            if(header != null) {
                userContacts.add(header);
            }
            userContacts.add(contact);
        }

    }

    private void loadTelegramContacts(Cursor c, List<Contact> telegramContacts) {
        // Get Contact ID
        int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
        String contactId = c.getString(idIndex);
        // Get Contact Name
        int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String contactDisplayName = c.getString(nameIndex);

        ContactType type = ContactType.TelegramContact;
        Contact contact = new Contact(contactId, contactDisplayName, type);

        fetchContactNumbers(c, contact);
        fetchContactEmails(c, contact);

        if(contact.getUser() != null) {
            telegramContacts.add(contact);
        }
    }

    public void fetchContactNumbers(Cursor cursor, Contact contact) {
        // Get numbers
        final String[] numberProjection = new String[]{Phone.NUMBER, Phone.TYPE,};

        Cursor phone = new CursorLoader(context, Phone.CONTENT_URI, numberProjection,
                Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(contact.id)}, null)
                .loadInBackground();

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(Phone.TYPE);

            while (!phone.isAfterLast()) {
                final String number = phone.getString(contactNumberColumnIndex);
                final TdApi.User user = userMap != null ? userMap.get(number) : null;
                if (user != null) {
                    contact.setUser(user);
                    contact.setType(ContactType.TelegramContact);
                }
                final int type = phone.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence phoneType = Phone.getTypeLabel(
                        context.getResources(), type, customLabel);
                contact.addNumber(number, phoneType.toString());
                phone.moveToNext();
            }

        }
        phone.close();
    }

    public void fetchContactEmails(Cursor cursor, Contact contact) {
        // Get email
        final String[] emailProjection = new String[]{Email.DATA, Email.TYPE};

        Cursor email = new CursorLoader(context, Email.CONTENT_URI, emailProjection,
                Email.CONTACT_ID + "= ?", new String[]{String.valueOf(contact.id)}, null)
                .loadInBackground();

        if (email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(Email.DATA);
            final int contactTypeColumnIndex = email.getColumnIndex(Email.TYPE);

            while (!email.isAfterLast()) {
                final String address = email.getString(contactEmailColumnIndex);
                final int type = email.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence emailType = Email.getTypeLabel(
                        context.getResources(), type, customLabel);
                contact.addEmail(address, emailType.toString());
                email.moveToNext();
            }

        }

        email.close();
    }
}
