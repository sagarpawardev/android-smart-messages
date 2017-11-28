package dev.sagar.smsblocker.tech.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.exceptions.NoContactPictureException;
import dev.sagar.smsblocker.tech.exceptions.NoSuchContactException;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class ContactUtilSingleton {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Constants
    private static final String TAG = "ContactUtilSingleton";

    //Java Core
    private static ContactUtilSingleton instance = null;

    /**
     * This method is part of Singleton Design pattern.
     */
    private ContactUtilSingleton(){}


    /**
     * This method is part of Singleton Design pattern.
     * @return Instance of ContactUtilSingleton
     */
    public static synchronized ContactUtilSingleton getInstance(){
        if(instance == null)
            instance = new ContactUtilSingleton();
        return instance;
    }


    /**
     * This method converts a phone number to Contract name
     * @param context Context of activity
     * @param phoneNumber Contact's Phone Number
     * @return Name of Contact from Phone Number
     */
    public String getContactName(Context context, String phoneNumber) {

        final String methodName = "getContactName()";
        log.info(methodName, "Just Entered...");

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = contentResolver.query(uri, new String[]{ ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            log.error(methodName, "==> Nothing in Cursor for "+phoneNumber);
            return null;
        }
        String contactName = phoneNumber;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        log.info(methodName, "Returing...");
        return contactName;
    }


    /**
     * This method returns list of all contacts present in Device
     * @param context Context of activity
     * @return List of all contacts
     */
    public ArrayList<Contact> getAllContacts(Context context){
        final String METHOD_NAME = "getAllContacts()";
        Log.e(TAG, "==>Inside "+METHOD_NAME);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String projection[] = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };
        String selection
                //= ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER +" > 1"
                = ""
         ;
        String selectionArg[] = {};
        String mOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        Cursor cursor = contentResolver.query(uri, projection , selection, selectionArg, mOrder);
        Log.e(TAG, "Reading Contacts...");
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Uri dpUri = Uri.parse(image_uri);

                Contact contact = new Contact();
                contact.setDisplayName(name);
                contact.setId(id);
                contact.setNumber(number);
                contact.setDp(dpUri);

                contacts.add(contact);
            }
        }

        catch (NullPointerException e){
            e.printStackTrace();
        }
        finally {
            if (cursor!=null) cursor.close();
        }

        Log.e(TAG, "==>Returning from "+METHOD_NAME);
        return contacts;
    }


    /**
     * This method returns a list of contacts based on the String pattern
     * @param context Context of activity
     * @param searchStr search String could be partial phone number or contact name
     * @return List of matched contacts
     */
    public ArrayList<Contact> searchContacts(Context context, String searchStr){
        final String METHOD_NAME = "searchContacts()";
        Log.e(TAG, "==>Inside "+METHOD_NAME);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String projection[] = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };
        String selection =
                ContactsContract.CommonDataKinds.Phone.NUMBER +" LIKE ? OR " +
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?"
                ;
        String selectionArg[] = {"%"+searchStr+"%", "%"+searchStr+"%"};
        String mOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        Cursor cursor = contentResolver.query(uri, projection , selection, selectionArg, mOrder);
        Log.e(TAG, "Reading Contacts...");
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Uri dpUri = Uri.parse(image_uri);

                Contact contact = new Contact();
                contact.setDisplayName(name);
                contact.setId(id);
                contact.setNumber(number);
                contact.setDp(dpUri);

                contacts.add(contact);
            }
        }

        catch (NullPointerException e){
            e.printStackTrace();
        }
        finally {
            if (cursor!=null) cursor.close();
        }

        Log.e(TAG, "==>Returning from "+METHOD_NAME);
        return contacts;
    }


    /**
     * This method gives URI of Picture of an contact
     * @param context Context of activity
     * @param phoneNo Contact's Phone Number
     * @return URI of Phone number
     */
    public Uri getPictureUri(Context context, String phoneNo){
        final String methodName = "getPictureUri()";
        log.info(methodName, "Just Entered..");
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        String projection[] = {
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
        };

        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER +"= ?";
        String selectionArg[] = {phoneNo};
        String mOrder = null;

        Cursor contactsCursor = null;
        Uri imgUri = null;
        try{
            contactsCursor = contentResolver.query(uri, projection, selection, selectionArg, mOrder);

            if(contactsCursor == null){
                throw new NoSuchContactException(phoneNo);
            }

            log.info(methodName, "Found Contacts: "+contactsCursor.getCount());
            contactsCursor.moveToNext();

            String image_uri = contactsCursor
                    .getString(contactsCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            if(image_uri == null){
                throw new NoContactPictureException(phoneNo);
            }

            imgUri = Uri.parse(image_uri);
            log.info(methodName, "Found Profile ficture...");

        }
        catch (NoSuchContactException ex){
            log.error(methodName, phoneNo+" Does not exist");
        }
        catch (NoContactPictureException ex){
            log.error(methodName, phoneNo+" Does not have a Picture");
        }
        catch (Exception ex){
            log.error(methodName, phoneNo+" got Some Unknown Exception: "+ex.getMessage());
        }
        finally {
            if(contactsCursor!=null && !contactsCursor.isClosed())
                contactsCursor.close();
        }

        log.info(methodName, "Returning...");
        return imgUri;
    }

}
