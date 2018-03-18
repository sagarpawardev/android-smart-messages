package dev.sagar.smsblocker.tech.service.helper.savedsms;

import android.provider.BaseColumns;

/**
 * Created by sagarpawar on 08/02/18.
 */

public final class SavedSMSDBAttributes {

    private SavedSMSDBAttributes() {}

    /* Inner class that defines the table contents */
    public static class SavedSMS implements BaseColumns {
        public static final String TABLE_NAME = "SAVEDSMS";

        public static final String COLUMN_NAME_ID = "sms_id";
        public static final String COLUMN_NAME_ADDRESS = "address";

    }
}
