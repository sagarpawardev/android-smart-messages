package dev.sagar.smsblocker.tech.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.HashMap;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.exceptions.NoSuchContactException;
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
        log.justEntered(methodName);

        Contact contact = null;
        try {
            contact = getContact(context, phoneNumber);
        } catch (NoSuchContactException e) {
            e.printStackTrace();
        }

        String name;
        if(contact == null){
            name = phoneNumber;
        }else{
            name = contact.getDisplayName();
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
            contact.setPhotoThumbnail(null);
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
                    contact.setPhotoThumbnail(imgUri);
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
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.TYPE
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
            String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(Phone.CONTACT_ID));
            String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
            int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));

            if(number == null){
                log.info(methodName, name+" Does not have a number and its id is: "+id);
                continue;
            }

            Contact contact = new Contact();
            contact.setDisplayName(name);
            contact.setId(id);
            contact.setNumber(number);

            String strType = null;

            /*switch (type){
                case Phone.TYPE_HOME:
                    strType = context.getString(R.string.txt_home);
                    break;
                case Phone.TYPE_MOBILE:
                    strType = context.getString(R.string.txt_mobile);
                    break;
                case Phone.TYPE_WORK:
                    strType = context.getString(R.string.txt_work);
                    break;
                case Phone.TYPE_FAX_HOME:
                    strType = context.getString(R.string.txt_home_fax);
                    break;
                case Phone.TYPE_FAX_WORK:
                    strType = context.getString(R.string.txt_work_fax);
                    break;
                case Phone.TYPE_MAIN:
                    strType = context.getString(R.string.txt_main);
                    break;
                case Phone.TYPE_OTHER:
                    strType = "Other";
                    break;
                case Phone.TYPE_CUSTOM:
                    strType = "Custom";
                    break;
                case Phone.TYPE_PAGER:
                    strType = "Pager";
                    break;
            }*/

            CharSequence seq = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                    context.getResources(),
                    type,
                    context.getString(R.string.txt_mobile)
            );
            contact.setType(seq.toString());




            String image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if(image_uri != null){
                Uri dpUri = Uri.parse(image_uri);
                contact.setPhotoThumbnail(dpUri);
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
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.TYPE
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
            int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));

            if(number == null){
                log.info(methodName, name+" Does not have a number and its id is: "+id);
                continue;
            }

            CharSequence seq = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                    context.getResources(),
                    type,
                    context.getString(R.string.txt_mobile)
            );

            Contact contact = new Contact();
            contact.setDisplayName(name);
            contact.setId(id);
            contact.setNumber(number);
            contact.setType(seq.toString());

            String image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if(image_uri != null){
                Uri dpUri = Uri.parse(image_uri);
                contact.setPhotoThumbnail(dpUri);
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

        Contact contact = getContactOrDefault(context, phoneNo);

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
                contact.setPhotoThumbnail(null);

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
                    contact.setPhotoThumbnail(imgUri);
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
        return contact.getPhotoThumbnail();
    }

    public Contact getContactOrDefault(Context context, String phoneNumber){
        final String methodName = "getContactOrDefault()";
        log.justEntered(methodName);

        Contact contact = null;
        try{
            //TODO Default more data here like Uri, Thumbnail Uri

            try {
                contact = getContact(context, phoneNumber);
            } catch (NoSuchContactException e) {
                log.info(methodName, "No Contact found for address : "+phoneNumber);
                e.printStackTrace();
            }

            if(contact != null) {
                String name = contact.getDisplayName() == null ? contact.getNumber() : contact.getDisplayName();

                contact.setDisplayName(name);
            }

        }
        catch (ReadContactPermissionException e){
            log.error(methodName, "Got null from contacts so defaulting phoneNu");
        }

        if(contact == null){
            contact = new Contact();
            contact.setId(null);
            String formatAddress = PhoneUtilsSingleton.getInstance().formatNumber(context, phoneNumber);
            contact.setDisplayName(formatAddress);
            contact.setPhoto(null);
            contact.setPhotoThumbnail(null);
            contact.setType(null);
            contact.setNumber(phoneNumber);
        }

        log.returning(methodName);
        return contact;
    }

    public Contact getContact(Context context, String phoneNumber) throws ReadContactPermissionException, NoSuchContactException {
        final String methodName = "getContact(Context, String)";
        log.justEntered(methodName);

        Contact result = new Contact();

        phoneNumber = PhoneUtilsSingleton.getInstance().formatNumber(context, phoneNumber);

        //Caching
        log.info(methodName, "Checking in cache");
        if(contactMap.containsKey(phoneNumber)) {
            log.info(methodName, phoneNumber+" Found in cache ");
            result = contactMap.get(phoneNumber);
        }
        else {

            //Check Permissions if donot have permission return behave like you don't have contact
            boolean hasContactPerm = PermissionUtilSingleton.getInstance().hasPermission(context, READ_CONTACTS);
            if (!hasContactPerm) {
                log.info(methodName, "No permissions for is given");
                throw new ReadContactPermissionException();
            }

            //Actual Procedure
            log.info(methodName, "Reading Content provider for contact: "+phoneNumber);
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            String[] projection = {
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup._ID,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI,
                    ContactsContract.PhoneLookup.PHOTO_URI,
                    ContactsContract.PhoneLookup.TYPE,
            };
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                log.error(methodName, "Nothing in Cursor for " + phoneNumber);
                result = null;
                throw new NoSuchContactException(phoneNumber);
            } else {
                log.info(methodName, "Cursor Size: " + cursor.getCount());
                if (cursor.moveToFirst()) {
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                    String thumb_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
                    String photo_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
                    String number = phoneNumber;

                    int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.PhoneLookup.TYPE));
                    CharSequence seq = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                            context.getResources(),
                            type,
                            context.getString(R.string.txt_mobile)
                    );

                    log.info(methodName, "Received contact data for: " + number);

                    result.setNumber(number);
                    result.setId(id);
                    result.setDisplayName(contactName);
                    result.setType(seq.toString());

                    if (thumb_uri != null) {
                        Uri imgUri = Uri.parse(thumb_uri);
                        result.setPhotoThumbnail(imgUri);
                        log.info(methodName, "Found Profile picture...");
                    } else {
                        log.info(methodName, "No picture for phone number: " + phoneNumber);
                    }

                    if (photo_uri != null) {
                        Uri imgUri = Uri.parse(photo_uri);
                        result.setPhoto(imgUri);
                        log.info(methodName, "Found Profile picture thumbnail...");
                    } else {
                        log.info(methodName, "No Thumbnail picture for phone number: " + phoneNumber);
                    }
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }

            //Cache Data
            contactMap.put(phoneNumber, result);
        }
        log.returning(methodName);

        return result;
    }

}
