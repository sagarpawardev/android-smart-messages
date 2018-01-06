package dev.sagar.smsblocker.tech.beans;

import android.provider.Telephony;

import java.io.Serializable;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class SMS implements Serializable{

    private String id,
            from,
            body;
    private long dateTime,
            type;
    private boolean read;

    private int subscription;

    public static final long TYPE_SENT = Telephony.Sms.MESSAGE_TYPE_SENT;
    public static final long TYPE_RECEIVED = Telephony.Sms.MESSAGE_TYPE_INBOX;
    public static final long TYPE_QUEUED = Telephony.Sms.MESSAGE_TYPE_QUEUED;
    public static final long TYPE_DRAFT = Telephony.Sms.MESSAGE_TYPE_DRAFT;
    public static final long TYPE_FAILED = Telephony.Sms.MESSAGE_TYPE_FAILED;
    public static final long TYPE_OUTBOX = Telephony.Sms.MESSAGE_TYPE_OUTBOX;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean state) {
        this.read = state;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public int getSubscription() {
        return subscription;
    }

    public void setSubscription(int subscription) {
        this.subscription = subscription;
    }
}
