package com.telegram.spektogram.contacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.HashSet;

// new ContactFetcher(this).fetchAll();
public class ContactFetcher {
	private Context context;
	private final HashSet<String> azIndexer =new HashSet<String>();
	public ContactFetcher(Context c) {
		this.context = c;
	}

	public ArrayList<Contact> fetchAll() {
		ArrayList<Contact> listContacts = new ArrayList<Contact>();
		String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 ";
		CursorLoader cursorLoader = new CursorLoader(context, ContactsContract.Contacts.CONTENT_URI,
				null, // the columns to retrieve (all)
				selection, // the selection criteria (none)
				null, // the selection args (none)
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC" // the sort order (default)
		);

		Cursor c = cursorLoader.loadInBackground();
		if (c.moveToFirst()) {
			do {
				 loadContactData(c,listContacts);
			} while (c.moveToNext());
		}
		c.close();
		return listContacts;
	}

	private void loadContactData(Cursor c, ArrayList<Contact> listContacts) {
		// Get Contact ID
		int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
		String contactId = c.getString(idIndex);
		// Get Contact Name
		int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		String contactDisplayName = c.getString(nameIndex);

		int type = BaseContactItem.TYPE_ITEM;
		final String substring = contactDisplayName.substring(0, 1);
		if(!azIndexer.contains(substring)){
			azIndexer.add(substring);
			type = BaseContactItem.TYPE_SEPARATOR;
			Contact header = new Contact(contactId, substring,type);
			listContacts.add(header);
		}

		type = BaseContactItem.TYPE_ITEM;
		Contact contact = new Contact(contactId, contactDisplayName,type);
		fetchContactNumbers(c, contact);
		fetchContactEmails(c, contact);
		listContacts.add(contact);

	}

	public void fetchContactNumbers(Cursor cursor, Contact contact) {
		// Get numbers
		final String[] numberProjection = new String[] { Phone.NUMBER, Phone.TYPE, };
		
		Cursor phone = new CursorLoader(context, Phone.CONTENT_URI, numberProjection,
				Phone.CONTACT_ID +" = ?", new String[] { String.valueOf(contact.id) }, null)
				.loadInBackground();

		if (phone.moveToFirst()) {
			final int contactNumberColumnIndex = phone.getColumnIndex(Phone.NUMBER);
			final int contactTypeColumnIndex = phone.getColumnIndex(Phone.TYPE);

			while (!phone.isAfterLast()) {
				final String number = phone.getString(contactNumberColumnIndex);
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
		final String[] emailProjection = new String[] { Email.DATA, Email.TYPE };

		Cursor email = new CursorLoader(context, Email.CONTENT_URI, emailProjection,
				Email.CONTACT_ID + "= ?", new String[] { String.valueOf(contact.id) }, null)
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
