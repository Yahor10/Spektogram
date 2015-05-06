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
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_TABLE_RESULT = "CREATE TABLE "
                + ConstantsDB.TABLE_CHATS + "(" + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ConstantsDB.COLUMN_CHAT_ID_TELEGRAM + " INTEGER NOT NULL UNIQUE,"
                + ConstantsDB.COLUMN_CHAT_TYPE + " INTEGER NOT NULL,"
                + ConstantsDB.COLUMN_CHAT_NAME + " TEXT NOT NULL"
                + ")";

        String CREATE_TABLE_USER = "CREATE TABLE "
                + ConstantsDB.TABLE_USERS + "("
                + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ConstantsDB.COLUMN_USER_ID_TELEGRAM + " INTEGER NOT NULL UNIQUE,"
                + ConstantsDB.COLUMN_USER_NAME + " TEXT,"
                + ConstantsDB.COLUMN_USER_LASTNAME + " TEXT,"
                + ConstantsDB.COLUMN_USER_FIRSTNAME + " TEXT NOT NULL,"
                + ConstantsDB.COLUMN_USER_PHONE + " TEXT"
                + ")";

        String CREATE_TABLE_USET_TO_CHAT = "CREATE TABLE "
                + ConstantsDB.TABLE_USER_TO_CHATS + "("
                + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT + " INTEGER,"
                + ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_USER + " INTEGER,"
                + "FOREIGN KEY( "
                + ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT + " ) REFERENCES "
                + ConstantsDB.TABLE_CHATS + " (" + ConstantsDB.COLUMN_CHAT_ID_TELEGRAM + " ) ON DELETE CASCADE,"
                + "FOREIGN KEY( "
                + ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_USER + " ) REFERENCES "
                + ConstantsDB.TABLE_USERS + " (" + ConstantsDB.COLUMN_USER_ID_TELEGRAM + " )"
                + ")";

        String CREATE_TABLE_MESSAGES = "CREATE TABLE " + ConstantsDB.TABLE_MESSAGES
                + "(" + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ConstantsDB.COLUMN_MESSAGE_ID_TELEGRAM + " INTEGER NOT NULL UNIQUE,"
                + ConstantsDB.COLUMN_MESSAGE_TEXT + " TEXT,"
                + ConstantsDB.COLUMN_MESSAGE_TIME + " INTEGER,"
                + ConstantsDB.COLUMN_MESSAGE_TYPE + " INTEGER,"
                + ConstantsDB.COLUMN_MESSAGE_SENT + " INTEGER,"
                + ConstantsDB.COLUMN_MESSAGE_DELIVERED + " INTEGER,"
                + ConstantsDB.COLUMN_MESSAGE_KEY_OF_CHAT + " INTEGER,"
                + ConstantsDB.COLUMN_MESSAGE_KEY_OF_USER + " INTEGER,"
                + "FOREIGN KEY( " + ConstantsDB.COLUMN_MESSAGE_KEY_OF_CHAT + " ) REFERENCES "
                + ConstantsDB.TABLE_CHATS + " (" + ConstantsDB.COLUMN_CHAT_ID_TELEGRAM + " ) ON DELETE CASCADE,"
                + "FOREIGN KEY( " + ConstantsDB.COLUMN_MESSAGE_KEY_OF_USER + " ) REFERENCES "
                + ConstantsDB.TABLE_USERS + " (" + ConstantsDB.COLUMN_USER_ID_TELEGRAM + " )"
                + ")";

        String CREATE_TABLE_LAST_MESSAGES = "CREATE TABLE " + ConstantsDB.TABLE_LAST_MESSAGE
                + "(" + ConstantsDB.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_CHAT + " INTEGER,"
                + ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_MESSAGE + " INTEGER,"
                + "FOREIGN KEY( "
                + ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_CHAT + " ) REFERENCES "
                + ConstantsDB.TABLE_CHATS + " (" + ConstantsDB.COLUMN_CHAT_ID_TELEGRAM + " ) ON DELETE CASCADE,"
                + "FOREIGN KEY( "
                + ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_MESSAGE + " ) REFERENCES "
                + ConstantsDB.TABLE_MESSAGES + " (" + ConstantsDB.COLUMN_MESSAGE_ID_TELEGRAM + " )"
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
                    values_chat.put(ConstantsDB.COLUMN_CHAT_TYPE, ConstantsDB.TYPE_CHAT_ONE_USER);
                } else if (chat.type instanceof TdApi.GroupChatInfo) {
                    chat_name = ((TdApi.GroupChatInfo) chat.type).groupChat.title;
                    values_chat.put(ConstantsDB.COLUMN_CHAT_TYPE, ConstantsDB.TYPE_CHAT_SEVERAL_USERS);
                }
            }

            values_chat.put(ConstantsDB.COLUMN_CHAT_NAME, chat_name);
            values_chat.put(ConstantsDB.COLUMN_CHAT_ID_TELEGRAM, chat.id);


            ContentValues values_user_to_chat = new ContentValues();

            values_user_to_chat.put(ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT, chat.id);
            if (chat.type instanceof TdApi.PrivateChatInfo || chat.type instanceof TdApi.UnknownPrivateChatInfo) {
                values_user_to_chat.put(ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_USER, ((TdApi.PrivateChatInfo) chat.type).user.id);
            } else {
                values_user_to_chat.put(ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_USER, 321);
            }

            SQLiteDatabase db = this.getWritableDatabase();

            long id_chat = db.insert(ConstantsDB.TABLE_CHATS, null, values_chat);
            long id_chat_user = db.insert(ConstantsDB.TABLE_USER_TO_CHATS, null, values_user_to_chat);

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
            values.put(ConstantsDB.COLUMN_USER_ID_TELEGRAM, user.id);


            SQLiteDatabase db = this.getWritableDatabase();

             long id = db.insert(ConstantsDB.TABLE_USERS, null, values);

            db.close();

        }

    }


    public ArrayList<TdApi.Chat> getAllChats() {

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
            int columnChatType = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_CHAT_TYPE);

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                do {

                    TdApi.Chat chat = new TdApi.Chat();


                    chat.id = cursor.getInt(columnIdChat);

                    if (cursor.getInt(columnChatType) == ConstantsDB.TYPE_CHAT_ONE_USER) {
                        chat.type = new TdApi.PrivateChatInfo();
                        ArrayList<TdApi.User> users = getUsersByChatId(db, chat.id);
                        if (users != null && users.size() > 0) {
                            ((TdApi.PrivateChatInfo) chat.type).user = users.get(0);
                        }
//                            !!!
                    } else if (cursor.getInt(columnChatType) == ConstantsDB.TYPE_CHAT_SEVERAL_USERS) {
                        chat.type = new TdApi.GroupChatInfo();
                        ((TdApi.GroupChatInfo) chat.type).groupChat = new TdApi.GroupChat();
                        ((TdApi.GroupChatInfo) chat.type).groupChat.title = cursor.getString(columnChatName);
                    }
//

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


        return returnChat;
    }

    private ArrayList<TdApi.User> getUsersByChatId(SQLiteDatabase db, long chat_id) {

        ArrayList<TdApi.User> users = null;

        String queryUserByChatId = "Select * FROM " + ConstantsDB.TABLE_USER_TO_CHATS
                + " WHERE " + ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_CHAT + " = " + "'"
                + chat_id + "'";
        Cursor cursor = db.rawQuery(queryUserByChatId, null);

        int columnUserId = cursor
                .getColumnIndex(ConstantsDB.COLUMN_USER_TO_CHAT_FOREIGN_KEY_USER);

        ArrayList<Integer> users_id = new ArrayList<Integer>();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            boolean flagFinishReadCursor = false;

            while (!flagFinishReadCursor) {
                int id;
                id = cursor.getInt(columnUserId);
                users_id.add(id);
                if (!cursor.moveToNext()) {
                    flagFinishReadCursor = true;
                }
            }
            cursor.close();

            users = getAllUsersByUserId(db, users_id);

        } else {
            users = null;
        }

        return users;
    }

    private ArrayList<TdApi.User> getAllUsersByUserId(SQLiteDatabase db, ArrayList<Integer> users_id) {

        ArrayList<TdApi.User> users = new ArrayList<TdApi.User>();

        if (users_id == null || users_id.size() == 0) {
            return null;
        } else {
            for (int id : users_id) {
                TdApi.User user = getUserById(db, id);
                if (user != null) {
                    users.add(user);
                }
            }
        }
        return users;

    }

    private TdApi.User getUserById(SQLiteDatabase db, int user_id) {


        String queryUserByUserId = "Select * FROM " + ConstantsDB.TABLE_USERS
                + " WHERE " + ConstantsDB.COLUMN_USER_ID_TELEGRAM + " = " + "'"
                + user_id + "'";
        Cursor cursor = db.rawQuery(queryUserByUserId, null);


        TdApi.User user;

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            user = new TdApi.User();

            user.id = user_id;
            int columnUserName = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_USER_NAME);
            int columnUserFirstName = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_USER_FIRSTNAME);
            int columnUserLastName = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_USER_LASTNAME);
            int columnUserPhone = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_USER_PHONE);
            user.username = cursor.getString(columnUserName);
            user.firstName = cursor.getString(columnUserFirstName);
            user.lastName = cursor.getString(columnUserLastName);
            user.phoneNumber = cursor.getString(columnUserPhone);

            cursor.close();
        } else {
            user = null;
        }


        return user;
    }


    public void addMessage(TdApi.Message message, long chat, int user) {

        if (message != null && chat >= 0 && user >= 0) {
            long chat_id = chat;
            long user_id = user;


            ContentValues values_message = new ContentValues();
            values_message.put(ConstantsDB.COLUMN_MESSAGE_ID_TELEGRAM, message.id);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_KEY_OF_CHAT, chat_id);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_KEY_OF_USER, user_id);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_TIME, message.date);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_SENT, 1);
            values_message.put(ConstantsDB.COLUMN_MESSAGE_DELIVERED, 1);

            if (message.message instanceof TdApi.MessageText) {
                values_message.put(ConstantsDB.COLUMN_MESSAGE_TEXT, ((TdApi.MessageText) message.message).text);
                values_message.put(ConstantsDB.COLUMN_MESSAGE_DELIVERED, ConstantsDB.TYPE_MESSAGE_TEXT);
            } else {
                values_message.put(ConstantsDB.COLUMN_MESSAGE_TEXT, "This message contains embedded data ");
                values_message.put(ConstantsDB.COLUMN_MESSAGE_DELIVERED, ConstantsDB.TYPE_MESSAGE_AUDIO);
            }


            ContentValues values_last_message = new ContentValues();
            values_last_message.put(ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_CHAT, chat_id);
            values_last_message.put(ConstantsDB.COLUMN_LAST_MESSAGE_KEY_OF_MESSAGE, message.id);


            SQLiteDatabase db = this.getWritableDatabase();

            long id_message = db.insert(ConstantsDB.TABLE_MESSAGES, null, values_message);
            long id_last_message = db.insert(ConstantsDB.TABLE_LAST_MESSAGE, null, values_last_message);

            db.close();
        }

    }

    public ArrayList<TdApi.Message> getAllMessagesFromChat(int chat_id) {

        ArrayList<TdApi.Message> returnMessages = new ArrayList<TdApi.Message>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {

            String queryTest = "Select * FROM " + ConstantsDB.TABLE_MESSAGES
                    + " WHERE " + ConstantsDB.COLUMN_MESSAGE_KEY_OF_CHAT + " = " + "'"
                    + chat_id + "'";

            Cursor cursor = db.rawQuery(queryTest, null);


            int columnIdtelegram = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_MESSAGE_ID_TELEGRAM);
            int columnText = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_MESSAGE_TEXT);
            int columnTime = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_MESSAGE_TIME);
            int columnDelivered = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_MESSAGE_DELIVERED);
            int columnSent = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_MESSAGE_SENT);
            int columnUserId = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_MESSAGE_KEY_OF_USER);
            int columnType = cursor
                    .getColumnIndex(ConstantsDB.COLUMN_MESSAGE_TYPE);

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                do {

                    TdApi.Message message = new TdApi.Message();


                    message.id = cursor.getInt(columnIdtelegram);
                    message.date = cursor.getInt(columnTime);
                    message.fromId = cursor.getInt(columnUserId);
                    message.chatId = chat_id;


                    if (cursor.getInt(columnType) == ConstantsDB.TYPE_MESSAGE_TEXT) {
                        message.message = new TdApi.MessageText();
                        ((TdApi.MessageText) message.message).text = cursor.getString(columnText);

//                            !!!
                    } else {
                        message.message = new TdApi.MessageText();
                        ((TdApi.MessageText) message.message).text = "отправка файлов в доработке";
                    }
//

                    returnMessages.add(message);
                } while (cursor.moveToNext());

                cursor.close();
                db.setTransactionSuccessful();
            } else {
                returnMessages = null;
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
