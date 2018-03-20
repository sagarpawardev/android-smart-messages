package dev.sagar.smsblocker.tech.service;

import android.net.Uri;
import android.provider.Telephony;

import dev.sagar.smsblocker.tech.service.helper.conversation.ConversationDBAttributes;
import dev.sagar.smsblocker.tech.service.helper.savedsms.SavedSMSDBAttributes;

/**
 * Created by sagarpawar on 08/02/18.
 */

public class DBConstants {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Sms.db";

    public static final String TABLE_CONVERSATION = ConversationDBAttributes.Converesation.TABLE_NAME;
    public static final String TABLE_SAVEDSMS = SavedSMSDBAttributes.SavedSMS.TABLE_NAME;
    public static final Uri URI_INBOX = Telephony.Sms.Inbox.CONTENT_URI;


}
