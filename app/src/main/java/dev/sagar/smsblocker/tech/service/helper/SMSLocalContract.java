package dev.sagar.smsblocker.tech.service.helper;

import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;

/**
 * Created by sagarpawar on 08/02/18.
 */

public final class SMSLocalContract {

    private SMSLocalContract() {}

    /* Inner class that defines the table contents */
    public static class SMSLocal implements BaseColumns {
        public static final String TABLE_NAME = "CONVERSATION";

        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_THREAD_ID = "thread_id";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_PERSON = "person";
        public static final String COLUMN_NAME_DATE = "time_in_millis";
        public static final String COLUMN_NAME_PROTOCOL = "protocol";
        public static final String COLUMN_NAME_READ = "read_status";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_REPLY_PATH_PRESENT = "reply_path_present";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_SERVICE_CENTER = "service_center";
        public static final String COLUMN_NAME_SUBSCRIPTION_ID = "subscription_id";
        public static final String COLUMN_NAME_LOCKED = "locked";
        public static final String COLUMN_NAME_ERROR_CODE = "error_code";

        public static final String COLUMN_NAME_PHOTO_URI = "photo_uri";
        public static final String COLUMN_NAME_PHOTO_THUMBNAIL = "photo_thumbnail_url";
        public static final String COLUMN_NAME_CONTACT_NAME = "display_name";

    }
}
