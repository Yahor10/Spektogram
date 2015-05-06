package com.telegram.spektogram.contacts;

/**
 * Created by ychabatarou on 04.05.2015.
 */
public class BaseContactItem {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;
    public static final int TYPE_ITEM_TELEGRAM = 3;

    public int type;

    public BaseContactItem(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
