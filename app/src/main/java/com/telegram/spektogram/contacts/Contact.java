package com.telegram.spektogram.contacts;

import com.telegram.spektogram.enums.ContactType;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

public class Contact extends BaseContactItem {
	public String id;
	public String name;
	public ArrayList<ContactEmail> emails;
	public ArrayList<ContactPhone> numbers;
	private TdApi.User user;

	public Contact(String id, String name,ContactType type) {
		super(type);
		this.id = id;
		this.name = name;
		this.emails = new ArrayList<ContactEmail>();
		this.numbers = new ArrayList<ContactPhone>();
	}


	@Override
	public String toString() {
		String number;
		if(!numbers.isEmpty()){
			number = numbers.get(0).number;
		} else{
			number= "";
		}
		return "Contact{" +
				"name='" + name + '\'' +
				", numbers=" + number + '\'' +
				", type=" + type +
				'}';
	}

	public TdApi.User getUser() {
		return user;
	}

	public void setUser(TdApi.User user) {
		this.user = user;
	}



	public void addEmail(String address, String type) {
		emails.add(new ContactEmail(address, type));
	}

	public void addNumber(String number, String type) {
		numbers.add(new ContactPhone(number, type));
	}


}
