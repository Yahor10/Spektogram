package com.telegram.spektogram.contacts;

import com.telegram.spektogram.enums.ContactType;

/**
 * Created by ychabatarou on 04.05.2015.
 */
public class BaseContactItem {



    public ContactType type;

    public BaseContactItem(ContactType type) {
        this.type = type;
    }

    public ContactType getType() {
        return type;
    }

    public void setType(ContactType type) {
        this.type = type;
    }
}
