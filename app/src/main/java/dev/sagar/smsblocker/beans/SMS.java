package dev.sagar.smsblocker.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class SMS{

    private String id,
            from,
            body;
    private long dateTime,
            type;
    private boolean read;

    public static final long TYPE_SENT = 2L;
    public static final long TYPE_RECEIVED = 1L;


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
}
