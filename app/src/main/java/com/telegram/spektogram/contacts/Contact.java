package com.telegram.spektogram.contacts;

import java.util.ArrayList;

public class Contact extends BaseContactItem {
	public String id;
	public String name;
	public ArrayList<ContactEmail> emails;
	public ArrayList<ContactPhone> numbers;

	public Contact(String id, String name,int type) {
		super(type);
		this.id = id;
		this.name = name;
		this.emails = new ArrayList<ContactEmail>();
		this.numbers = new ArrayList<ContactPhone>();
	}

	@Override
	public String toString() {
		String result = name;
		if (numbers.size() > 0) {
			ContactPhone number = numbers.get(0);
			result += " (" + number.number + " - " + number.type + ")";
		}
		if (emails.size() > 0) {
			ContactEmail email = emails.get(0);
			result += " [" + email.address + " - " + email.type + "]";
		}
		return result;
	}

	public void addEmail(String address, String type) {
		emails.add(new ContactEmail(address, type));
	}

	public void addNumber(String number, String type) {
		numbers.add(new ContactPhone(number, type));
	}
}
