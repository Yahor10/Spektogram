package com.telegram.spektogram.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

/**
 * Created by alex-pers on 4/30/15.
 */
public class SpectrDBHandler extends SQLiteOpenHelper {

    public SpectrDBHandler(Context context) {
        super(context, ConstantsDB.DATABASE_NAME, null, ConstantsDB.DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_TABLE_RESULT = "CREATE TABLE "
                + ConstantsDB.TABLE_CHATS + "(" + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ConstantsDB.COLUMN_CHAT_ID_TELEGRAM + " INTEGER,"
                + ConstantsDB.COLUMN_CHAT_TYPE + " INTEGER,"
                + ConstantsDB.COLUMN_CHAT_NAME + " TEXT"
                + ")";

        String CREATE_TABLE_USER = "CREATE TABLE "
                + ConstantsDB.TABLE_USERS + "("
                + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ConstantsDB.COLUMN_USER_NAME + " TEXT,"
                + ConstantsDB.COLUMN_USER_LASTNAME + " TEXT,"
                + ConstantsDB.COLUMN_USER_FIRSTNAME + " TEXT,"
                + ConstantsDB.COLUMN_USER_PHONE + " TEXT,"
                + ")";

        String CREATE_TABLE_MESSAGES = "CREATE TABLE " + ConstantsDB.TABLE_MESSAGES
                + "(" + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ConstantsDB.COLUMN_MESSAGE_TEXT + " TEXT,"
                + ConstantsDB.COLUMN_MESSAGE_TIME + " INTEGER,"
                + ConstantsDB.COLUMN_MESSAGE_SENT + " INTEGER,"
                + ConstantsDB.COLUMN_MESSAGE_DELIVERED + " INTEGER,"
                + "FOREIGN KEY( " + ConstantsDB.COLUMN_MESSAGE_KEY_OF_CHAT + " ) REFERENCES "
                + ConstantsDB.TABLE_CHATS + " (" + ConstantsDB.COLUMN_ID + " )"
                + "FOREIGN KEY( " + ConstantsDB.COLUMN_MESSAGE_KEY_OF_USER + " ) REFERENCES "
                + ConstantsDB.TABLE_USERS + " (" + ConstantsDB.COLUMN_ID + " )"
                + ")";

        String CREATE_TABLE_LAST_MESSAGES = "CREATE TABLE " + ConstantsDB.TABLE_LAST_MESSAGE
                + "(" + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + "FOREIGN KEY( "
                + ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_CHAT + " ) REFERENCES "
                + ConstantsDB.TABLE_CHATS + " (" + ConstantsDB.COLUMN_ID + " )"
                + "FOREIGN KEY( "
                + ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_MESSAGE + " ) REFERENCES "
                + ConstantsDB.TABLE_MESSAGES + " (" + ConstantsDB.COLUMN_ID + " )"
                + ")";


        String CREATE_TABLE_USET_TO_CHAT = "CREATE TABLE "
                + ConstantsDB.TABLE_USER_TO_CHATS + "("
                + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + "FOREIGN KEY( "
                + ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT + " ) REFERENCES "
                + ConstantsDB.TABLE_CHATS + " (" + ConstantsDB.COLUMN_ID + " )"
                + "FOREIGN KEY( "
                + ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_USER + " ) REFERENCES "
                + ConstantsDB.TABLE_USERS + " (" + ConstantsDB.COLUMN_ID + " )"
                + ")";


        db.execSQL(CREATE_TABLE_RESULT);
        db.execSQL(CREATE_TABLE_MESSAGES);
        db.execSQL(CREATE_TABLE_LAST_MESSAGES);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_USET_TO_CHAT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL("DROP TABLE IF EXISTS " + ConstantsDB.TABLE_CHATS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantsDB.TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantsDB.TABLE_LAST_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantsDB.TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantsDB.TABLE_USER_TO_CHATS);

        onCreate(db);

    }


    public void addChat(TdApi.Chat chat) {

        if (chat != null) {
            String chat_name = "Spectogram";


            ContentValues values_chat = new ContentValues();

            if (chat.type != null) {
                if (chat.type instanceof TdApi.PrivateChatInfo) {
                    chat_name = ((TdApi.PrivateChatInfo) chat.type).user.firstName;
                    values_chat.put(ConstantsDB.COLUMN_CHAT_TYPE,ConstantsDB.TYPE_CHAT_ONE_USER);
                } else if (chat.type instanceof TdApi.GroupChatInfo) {
                    chat_name = ((TdApi.GroupChatInfo) chat.type).groupChat.title;
                    values_chat.put(ConstantsDB.COLUMN_CHAT_TYPE,ConstantsDB.TYPE_CHAT_SEVERAL_USERS);
                }
            }

            values_chat.put(ConstantsDB.COLUMN_CHAT_NAME, chat_name);
            values_chat.put(ConstantsDB.COLUMN_CHAT_ID_TELEGRAM, chat.id);


            ContentValues values_user_to_chat = new ContentValues();;
            values_user_to_chat.put(ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT, chat.id);
            if (chat.type instanceof TdApi.PrivateChatInfo || chat.type instanceof TdApi.UnknownPrivateChatInfo) {
                values_user_to_chat.put(ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT, ((TdApi.PrivateChatInfo) chat.type).user.id);
            }else{
                values_user_to_chat.put(ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT, -1);
            }

            SQLiteDatabase db = this.getWritableDatabase();

            db.insert(ConstantsDB.TABLE_CHATS, null, values_chat);

            db.insert(ConstantsDB.TABLE_USER_TO_CHATS, null, values_user_to_chat);

            db.close();
        }

    }

    public void addUser(TdApi.User user) {


        if (user != null) {
            String user_name = "unknown user";
            String user_first_name = "unknown first name";
            String user_last_name = "unknown last name";
            String user_phone = "unknown phone";


            if (!"".equals(user.firstName)) {
                user_first_name = user.firstName;
            }

            if (!"".equals(user.lastName)) {
                user_last_name = user.lastName;
            }

            if (!"".equals(user.username)) {
                user_name = user.username;
            }

            if (!"".equals(user.phoneNumber)) {
                user_phone = user.phoneNumber;
            }

            ContentValues values = new ContentValues();
            values.put(ConstantsDB.COLUMN_USER_NAME, user_name);
            values.put(ConstantsDB.COLUMN_USER_FIRSTNAME, user_first_name);
            values.put(ConstantsDB.COLUMN_USER_LASTNAME, user_last_name);
            values.put(ConstantsDB.COLUMN_USER_PHONE, user_phone);


            SQLiteDatabase db = this.getWritableDatabase();

            db.insert(ConstantsDB.TABLE_USERS, null, values);
            db.close();

        }

    }


    public void putMessage(TdApi.Message message, TdApi.Chat chat, TdApi.User user) {

        if (message != null && chat != null && user != null) {
            long chat_id = chat.id;
            long user_id = user.id;


            ContentValues values_message = new ContentValues();
            values_message.put(ConstantsDB.COLUMN_MESSAGE_KEY_OF_CHAT, chat_id);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_KEY_OF_USER, user_id);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_TIME, message.date);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_SENT, 1);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_DELIVERED, 1);

            if (message.message instanceof TdApi.MessageText) {
                values_message.put(ConstantsDB.COLUMN_MESSAGE_TEXT, ((TdApi.MessageText) message.message).text);
            } else {
                values_message.put(ConstantsDB.COLUMN_MESSAGE_TEXT, "This message contains embedded data ");
            }


            ContentValues values_last_message = new ContentValues();
            values_last_message.put(ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_CHAT, chat_id);
            values_last_message.put(ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_MESSAGE, message.id);


            SQLiteDatabase db = this.getWritableDatabase();

            db.insert(ConstantsDB.TABLE_MESSAGES, null, values_message);
            db.insert(ConstantsDB.TABLE_LAST_MESSAGE, null, values_last_message);

            db.close();
        }

    }

    public ArrayList<TdApi.Chat> getAllChats(){

        ArrayList<TdApi.Chat> returnChat = new ArrayList<TdApi.Chat>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {

            String queryTest = "Select * FROM " + ConstantsDB.TABLE_CHATS;

            Cursor cursor = db.rawQuery(queryTest, null);

            int columnIdChat = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_CHAT_ID_TELEGRAM);
            int columnChatName = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_CHAT_NAME);

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                do {

                    TdApi.Chat chat = new TdApi.Chat();



                    chat.id = cursor.getInt(columnIdChat);
                    if (chat.type != null) {
                        if (chat.type instanceof TdApi.PrivateChatInfo) {
                              chat.type = new TdApi.PrivateChatInfo();
//                            !!!
                        } else if (chat.type instanceof TdApi.GroupChatInfo) {
//                            chat_name = ((TdApi.GroupChatInfo) chat.type).groupChat.title;
                        }
                    }
//                    test.setNameTest(cursor.getString(columnTestName));
//                    test.setDescribeTest(cursor.getString(columnDescribeTest));
//                    test.setTypeTest(cursor.getInt(columnTypeTest));
//                    test.setLanguage(cursor.getString(columnLanguage));
//                    long id_key = cursor.getLong(columnIdKEY);
                    // test.setTasks(getTasks(id_key));

                    returnChat.add(chat);
                } while (cursor.moveToNext());

                cursor.close();
                db.setTransactionSuccessful();
            } else {
                returnChat = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.endTransaction();
        }
        db.close();


        return null;
    }


}
