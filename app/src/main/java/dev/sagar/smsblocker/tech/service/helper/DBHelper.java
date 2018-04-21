package dev.sagar.smsblocker.tech.service.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dev.sagar.smsblocker.tech.service.DBConstants;
import dev.sagar.smsblocker.tech.service.helper.conversation.ConversationDBAttributes;
import dev.sagar.smsblocker.tech.service.helper.savedsms.SavedSMSDBAttributes;

/**
 * Created by sagarpawar on 08/02/18.
 */

public class DBHelper extends SQLiteOpenHelper{

    private static final String CONVERSATION_CREATE_TABLE =
            "CREATE TABLE " + ConversationDBAttributes.Converesation.TABLE_NAME + " (" +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_ID + " TEXT " +
                    //"PRIMARY KEY"  //If some other sms app is handling db and it keps duplicate id then App wil keep on failing
                    "," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_THREAD_ID + " TEXT UNIQUE," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_ADDRESS + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_PERSON + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_DATE + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_PROTOCOL + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_READ + " INTEGER," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_STATUS + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_TYPE + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_REPLY_PATH_PRESENT + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_SUBJECT + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_BODY + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_SERVICE_CENTER + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_LOCKED + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_ERROR_CODE + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_SUBSCRIPTION_ID + " TEXT," +

                    ConversationDBAttributes.Converesation.COLUMN_NAME_UNREAD_COUNT+ " INTEGER," +

                    ConversationDBAttributes.Converesation.COLUMN_NAME_PHOTO_URI + " TEXT," +
                    ConversationDBAttributes.Converesation.COLUMN_NAME_PHOTO_THUMBNAIL + " TEXT," +

                    ConversationDBAttributes.Converesation.COLUMN_NAME_CONTACT_NAME + " TEXT" +
            ")";

    private static final String CONVERSATION_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ConversationDBAttributes.Converesation.TABLE_NAME;

    private static final String SAVEDSMS_CREATE_TABLE =
            "CREATE TABLE " + SavedSMSDBAttributes.SavedSMS.TABLE_NAME + " (" +
                    SavedSMSDBAttributes.SavedSMS.COLUMN_NAME_ID + " TEXT, " +
                    SavedSMSDBAttributes.SavedSMS.COLUMN_NAME_ADDRESS + " TEXT, " +
                    SavedSMSDBAttributes.SavedSMS.COLUMN_DATE_ADDED + " TEXT " +
                    ")";

    private static final String SAVEDSMS_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + SavedSMSDBAttributes.SavedSMS.TABLE_NAME;


    public DBHelper(Context context){
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CONVERSATION_CREATE_TABLE);
        db.execSQL(SAVEDSMS_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(CONVERSATION_DELETE_TABLE);
        db.execSQL(SAVEDSMS_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
