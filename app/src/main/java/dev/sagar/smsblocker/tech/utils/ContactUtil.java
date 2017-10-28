package dev.sagar.smsblocker.tech.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

import dev.sagar.smsblocker.tech.beans.Contact;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class ContactUtil {
    //Constants
    private static final String TAG = "ContactUtil";


    //Convert Contact Number to Contact Name
    public static String getContactName(Context context, String phoneNumber) {
        final String METHOD_NAME = "getContactName()";
        Log.e(TAG, "==>Inside "+METHOD_NAME);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = contentResolver.query(uri, new String[]{ ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            Log.e(TAG, METHOD_NAME + "==>Some Query Cursor is null");
            return null;
        }
        String contactName = phoneNumber;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        Log.e(TAG, "==>Returning Contact Name:"+ contactName +" from"+METHOD_NAME);
        return contactName;
    }

    //Get All Contacts
    public static ArrayList<Contact> getAllContacts(Context context){
        final String METHOD_NAME = "getAllContacts()";
        Log.e(TAG, "==>Inside "+METHOD_NAME);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String projection[] = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
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

                Contact contact = new Contact();
                contact.setDisplayName(name);
                contact.setId(id);
                contact.setNumber(number);

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

    //Search Contacts in Phone
    public static ArrayList<Contact> searchContacts(Context context, String searchStr){
        final String METHOD_NAME = "searchContacts()";
        Log.e(TAG, "==>Inside "+METHOD_NAME);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String projection[] = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
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

                Contact contact = new Contact();
                contact.setDisplayName(name);
                contact.setId(id);
                contact.setNumber(number);

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
}
