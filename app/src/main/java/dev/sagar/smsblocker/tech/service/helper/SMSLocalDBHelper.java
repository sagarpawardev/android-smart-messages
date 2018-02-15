package dev.sagar.smsblocker.tech.service.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import dev.sagar.smsblocker.tech.service.helper.SMSLocalContract.SMSLocal;

/**
 * Created by sagarpawar on 08/02/18.
 */

public class SMSLocalDBHelper extends SQLiteOpenHelper{

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SMSLocal.TABLE_NAME + " (" +
                    SMSLocal.COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                    SMSLocal.COLUMN_NAME_THREAD_ID + " TEXT," +
                    SMSLocal.COLUMN_NAME_ADDRESS + " TEXT UNIQUE," +
                    SMSLocal.COLUMN_NAME_PERSON + " TEXT," +
                    SMSLocal.COLUMN_NAME_DATE + " TEXT," +
                    SMSLocal.COLUMN_NAME_PROTOCOL + " TEXT," +
                    SMSLocal.COLUMN_NAME_READ + " TEXT," +
                    SMSLocal.COLUMN_NAME_STATUS + " TEXT," +
                    SMSLocal.COLUMN_NAME_TYPE + " TEXT," +
                    SMSLocal.COLUMN_NAME_REPLY_PATH_PRESENT + " TEXT," +
                    SMSLocal.COLUMN_NAME_SUBJECT + " TEXT," +
                    SMSLocal.COLUMN_NAME_BODY + " TEXT," +
                    SMSLocal.COLUMN_NAME_SERVICE_CENTER + " TEXT," +
                    SMSLocal.COLUMN_NAME_LOCKED + " TEXT," +
                    SMSLocal.COLUMN_NAME_ERROR_CODE + " TEXT," +
                    SMSLocal.COLUMN_NAME_SUBSCRIPTION_ID + " TEXT," +

                    SMSLocal.COLUMN_NAME_PHOTO_URI + " TEXT," +
                    SMSLocal.COLUMN_NAME_PHOTO_THUMBNAIL + " TEXT," +

                    SMSLocal.COLUMN_NAME_CONTACT_NAME + " TEXT" +
            ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SMSLocal.TABLE_NAME;


    public SMSLocalDBHelper(Context context){
        super(context, SMSLocalDBConstants.DATABASE_NAME, null, SMSLocalDBConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
