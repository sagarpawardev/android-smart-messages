package dev.sagar.smsblocker.tech.beans;

import android.content.Context;
import android.net.Uri;

import dev.sagar.smsblocker.tech.exceptions.ReadContactPermissionException;
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;

/**
 * Created by sagarpawar on 05/02/18.
 */

public class Conversation {
    private String smsId;
    private String threadId, address,
            body, contactName;
    private long dateTime,
            type;
    private int msgCount=0;
    private int subscriptionId;
    private boolean readState;
    private Uri photoUri, photoThumbnailUri;
    private int unreadCount = 0;

    public Conversation(Context context, SMS sms){
        setAddress(sms.getAddress());
        setThreadId(sms.getAddress());
        setMsgCount(-1);
        setDateTime(sms.getDateTime());
        setBody(sms.getBody());
        setType(sms.getType());
        setSmsId(sms.getId());

        String contactName = sms.getAddress();
        try {
            contactName = ContactUtilSingleton.getInstance().getContactName(context, sms.getAddress());
        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }
        setContactName(contactName);

        setDateTime(sms.getDateTime());
        setType(sms.getType());
        setSubscriptionId(sms.getSubscription());
        setReadState(sms.isRead());

        Uri photoUri = null;
        try {
            photoUri = ContactUtilSingleton.getInstance().getPictureUri(context, sms.getAddress());
        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }
        setPhotoUri(photoUri);

        Uri photoThumbUri = null;
        try {
            photoThumbUri = ContactUtilSingleton.getInstance().getPictureUri(context, sms.getAddress());
        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }
        setPhotoThumbnailUri(photoThumbUri);

    }

    public Conversation(){}

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public Uri getPhotoThumbnailUri() {
        return photoThumbnailUri;
    }

    public void setPhotoThumbnailUri(Uri photoThumbnailUri) {
        this.photoThumbnailUri = photoThumbnailUri;
    }

    public boolean isReadState() {
        return readState;
    }

    public void setReadState(boolean readState) {
        this.readState = readState;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String from) {
        this.address = from;
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

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public boolean isRead(){
        return readState;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
