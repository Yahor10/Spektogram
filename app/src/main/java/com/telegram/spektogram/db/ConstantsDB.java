package com.telegram.spektogram.db;

/**
 * Created by alex-pers on 4/30/15.
 */
public class ConstantsDB {

    public static final String DATABASE_NAME = "SpectogramDB.db";

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CHATS = "chats";
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_LAST_MESSAGE = "last_message";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_USER_TO_CHATS = "user_to_chat";

    public static final String COLUMN_ID = "_id";

    // ======================================
    public static final String COLUMN_CHAT_NAME = "chat_name";
    public static final String COLUMN_CHAT_ID_TELEGRAM = "chat_id_telegram";
    public static final String COLUMN_CHAT_TYPE = "chat_type";



    // ======================================

    public static final String COLUMN_MESSAGE_KEY_OF_CHAT = "key_of_chat";
    public static final String COLUMN_MESSAGE_ID_TELEGRAM = "message_id_telegram";
    public static final String COLUMN_MESSAGE_TEXT = "message_text";
    public static final String COLUMN_MESSAGE_TIME = "time_of_message";
    public static final String COLUMN_MESSAGE_DELIVERED = "delivered";
    public static final String COLUMN_MESSAGE_SENT = "sent";
    public static final String COLUMN_MESSAGE_TYPE = "type_message";
    public static final String COLUMN_MESSAGE_KEY_OF_USER = "key_of_user";

    // ======================================

    public static final String COLUMN_LAST_MESSAGE_KEY_OF_CHAT = "key_of_chat";
    public static final String COLUMN_LAST_MESSAGE_KEY_OF_MESSAGE = "key_of_message";


    // ======================================

    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_ID_TELEGRAM = "user_id_telegram";
    public static final String COLUMN_USER_LASTNAME = "user_last_name";
    public static final String COLUMN_USER_FIRSTNAME = "user_first_name";
    public static final String COLUMN_USER_PHONE = "user_phone";

    // ======================================

    public static final String COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT = "id_chat_foreign";
    public static final String COLUMN_USER_TO_CHAT_FOREIGN_KEY_USER = "id_user_foreign";

    // ======================================

    public static final int  TYPE_CHAT_ONE_USER = 1;
    public static final int  TYPE_CHAT_SEVERAL_USERS = 2;

    // ======================================
    public static final int  TYPE_MESSAGE_TEXT = 1;
    public static final int  TYPE_MESSAGE_AUDIO = 2;
    public static final int  TYPE_MESSAGE_VIDEO = 3;

}
