package dev.sagar.smsblocker.tech.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.exceptions.ReadContactPermissionException;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class ContactUtilSingleton {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Constants
    private static final String TAG = "ContactUtilSingleton";
    final String[] ALL_PERMISSIONS = Permission.ALL;
    final String READ_SMS = Permission.READ_SMS;
    final String RECEIVE_SMS = Permission.RECEIVE_SMS;
    final String SEND_SMS = Permission.SEND_SMS;
    final String READ_CONTACTS = Permission.READ_CONTACTS;

    //Java Core
    private static ContactUtilSingleton instance = null;
    private static HashMap<String, Contact> contactMap = new HashMap<>();

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
    public String getContactName(Context context, String phoneNumber) throws ReadContactPermissionException {
        final String methodName = "getContactName()";
        log.info(methodName, "Just Entered...");

        Contact contact =  getContact(context, phoneNumber);
        String name = contact.getDisplayName();

        if(name == null){
            name = phoneNumber;
        }


        /*//Caching
        if(contactMap.containsKey(phoneNumber)) {
            String name = contactMap.get(phoneNumber).getDisplayName();
            return name;
        }

        //Check Permissions if donot have permission return behave like you don't have contact
        boolean hasContactPerm = PermissionUtilSingleton.getInstance().hasPermission(context, READ_CONTACTS);
        if(!hasContactPerm){
            return phoneNumber;
        }

        Contact contact = new Contact();

        //Actual Procedure
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String projection[] = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor == null) {
            log.error(methodName, "Nothing in Cursor for "+phoneNumber);
            contact.setDp(null);
            contact.setDisplayName(null);
            contact.setSubscriptionId(null);
            contact.setNumber(phoneNumber);
        }
        else {
            String contactName = phoneNumber;

            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                String thumb_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contact.setNumber(number);
                contact.setSubscriptionId(id);
                contact.setDisplayName(contactName);
                if(thumb_uri != null){
                    Uri imgUri = Uri.parse(thumb_uri);
                    contact.setDp(imgUri);
                    log.info(methodName, "Found Profile ficture...");
                }
                else{
                    log.info(methodName, "No picture for phone number: "+phoneNumber);
                }
            }

            if (!cursor.isClosed()) {
                cursor.close();
            }
        }

        contactMap.put(phoneNumber, contact);
        String name = contact.getDisplayName();
        if(name == null){
            name = phoneNumber;
        }*/

        log.returning(methodName);
        return name;
    }


    /**
     * This method returns list of all contacts present in Device
     * @param context Context of activity
     * @return List of all contacts
     */
    public ArrayList<Contact> getAllContacts(Context context) throws ReadContactPermissionException{
        final String methodName = "getAllContacts()";
        log.info(methodName, "Just Entered..");

        //Checking Permission
        boolean hasReadContactPermission = PermissionUtilSingleton.getInstance().hasPermission(context, READ_CONTACTS);
        if(!hasReadContactPermission){
            throw  new ReadContactPermissionException();
        }

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
        log.info(methodName, "Reading Contacts...");
        ArrayList<Contact> contacts = new ArrayList<>();
        if(cursor == null){
            log.error(methodName, "Received Cursor: "+null);
            return contacts;
        }

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(number == null){
                log.info(methodName, name+" Does not have a number and its id is: "+id);
                continue;
            }

            Contact contact = new Contact();
            contact.setDisplayName(name);
            contact.setId(id);
            contact.setNumber(number);

            String image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if(image_uri != null){
                Uri dpUri = Uri.parse(image_uri);
                contact.setDp(dpUri);
            }
            else {
                log.info(methodName, number+" Does Not have picture");
            }

            contacts.add(contact);

        }

        if (!cursor.isClosed()) cursor.close();
        log.returning(methodName);
        return contacts;
    }


    /**
     * This method returns a list of contacts based on the String pattern
     * @param context Context of activity
     * @param searchStr search String could be partial phone number or contact name
     * @return List of matched contacts
     */
    public ArrayList<Contact> searchContacts(Context context, String searchStr) throws ReadContactPermissionException{
        final String methodName = "searchContacts()";
        log.justEntered(methodName);

        //Checking Permission
        boolean hasReadContactPermission = PermissionUtilSingleton.getInstance().hasPermission(context, READ_CONTACTS);
        if(!hasReadContactPermission){
            throw  new ReadContactPermissionException();
        }

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
        log.info(methodName, "Reading Contacts...");
        ArrayList<Contact> contacts = new ArrayList<>();
        if(cursor == null){
            log.error(methodName, "Received Cursor: "+null);
            return contacts;
        }
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            if(number == null){
                log.info(methodName, name+" Does not have a number and its id is: "+id);
                continue;
            }

            Contact contact = new Contact();
            contact.setDisplayName(name);
            contact.setId(id);
            contact.setNumber(number);

            String image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if(image_uri != null){
                Uri dpUri = Uri.parse(image_uri);
                contact.setDp(dpUri);
            }
            else {
                log.info(methodName, number+" Does Not have picture");
            }

            contacts.add(contact);
        }
        cursor.close();

        log.info(methodName, "Returning..");
        return contacts;
    }


    /**
     * This method gives URI of Picture of an contact
     * @param context Context of activity
     * @param phoneNo Contact's Phone Number
     * @return URI of Phone number
     */
    public Uri getPictureUri(Context context, String phoneNo) throws ReadContactPermissionException {
        final String methodName = "getPictureUri()";
        log.justEntered(methodName);

        Contact contact = getContact(context, phoneNo);

        /*//Caching
        if(contactMap.containsKey(phoneNo)){
            contact = contactMap.get(phoneNo);
        }else {

            //Actual Procedure
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNo));
            ContentResolver contentResolver = context.getContentResolver();
            String[] projection = {
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup._ID,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            //String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "= ?";
            String selection = "";
            //String selectionArg[] = {phoneNo};
            String selectionArg[] = {};
            String mOrder = "";

            Cursor cursor = null;
            cursor = contentResolver.query(uri, projection, selection, selectionArg, mOrder);

            //If Contact is Not Found
            if (cursor == null || cursor.getCount() == 0) {
                contact.setNumber(phoneNo);
                contact.setSubscriptionId(null);
                contact.setDisplayName(null);
                contact.setDp(null);

                log.info(methodName, "Contact Not Found: " + phoneNo);
            }
            //If contact is Found
            else{
                log.info(methodName, "Found Contacts: " + cursor.getCount());
                cursor.moveToNext();

                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                String thumb_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                contact.setNumber(number);
                contact.setSubscriptionId(id);
                contact.setDisplayName(contactName);
                if(thumb_uri != null){
                    Uri imgUri = Uri.parse(thumb_uri);
                    contact.setDp(imgUri);
                    log.info(methodName, "Found Profile ficture...");
                }
                else{
                    log.info(methodName, "No picture for phone number: "+phoneNo);
                }

                if (!cursor.isClosed())
                    cursor.close();
            }

        }

        contactMap.put(phoneNo, contact);*/

        log.returning(methodName);
        return contact.getDp();
    }


    public Contact getContact(Context context, String phoneNumber) throws ReadContactPermissionException {
        final String methodName = "getContact()";
        log.justEntered(methodName);

        Contact result = new Contact();

        //Caching
        if(contactMap.containsKey(phoneNumber)) return contactMap.get(phoneNumber);

        //Check Permissions if donot have permission return behave like you don't have contact
        boolean hasContactPerm = PermissionUtilSingleton.getInstance().hasPermission(context, READ_CONTACTS);
        if(!hasContactPerm){
            throw new ReadContactPermissionException();
        }

        //Actual Procedure
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI,
        };
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor == null) {
            log.error(methodName, "Nothing in Cursor for "+phoneNumber);
            result.setDp(null);
            result.setId(null);
            result.setDisplayName(null);
            result.setNumber(phoneNumber);
        }
        else{
            if(cursor.moveToFirst()) {
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                String thumb_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
                String number = phoneNumber;

                result.setNumber(number);
                result.setId(id);
                result.setDisplayName(contactName);
                if(thumb_uri != null){
                    Uri imgUri = Uri.parse(thumb_uri);
                    result.setDp(imgUri);
                    log.info(methodName, "Found Profile ficture...");
                }
                else{
                    log.info(methodName, "No picture for phone number: "+phoneNumber);
                }

            }
            if(!cursor.isClosed()) {
                cursor.close();
            }
        }

        //Cache Data
        contactMap.put(phoneNumber, result);
        log.returning(methodName);

        return result;
    }

}
