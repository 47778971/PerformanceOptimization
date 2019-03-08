package com.jun.po.util;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.jun.po.model.ContactsInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContactsHelper {

    private static ContactsHelper contactsHelper;

    public static ContactsHelper getInstance() {
        if (null == contactsHelper) {
            contactsHelper = new ContactsHelper();
        }
        return contactsHelper;
    }

    private ContactsHelper() {
    }

    public String getContacts(Context context) {
        return getPhoneContacts(context).append(getSimContacts(context)).toString();
    }

    private StringBuilder getPhoneContacts(Context context) {
        StringBuilder phoneContacts = new StringBuilder();
        Cursor cursor = null;
        Map<String, List<String>> phoneNumbers = new HashMap();
        try {
            ContentResolver cr = context.getContentResolver();
            String str[] = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_ID};
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, str, null, null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                    if (phoneNumbers.get(name) == null) {
                        List<String> numbers = new ArrayList<>();
                        numbers.add(phone);
                        phoneNumbers.put(name, numbers);
                    } else {
                        List<String> numbers = phoneNumbers.get(name);
                        numbers.add(phone);
                        phoneNumbers.put(name, numbers);
                    }
                }

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        }
        return dealFormat(phoneNumbers);
    }

    private StringBuilder getSimContacts(Context context) {
        Cursor cursor = null;
        Map<String, List<String>> phoneNumbers = new HashMap();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://icc/adn");
            cursor = contentResolver.query(uri, null, null, null, null);
            if (null != cursor) {
                while (cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String phone = cursor.getString(cursor.getColumnIndex("number")).replace(" ", "");
                    if (phoneNumbers.get(name) == null) {
                        List<String> numbers = new ArrayList<>();
                        numbers.add(phone);
                        phoneNumbers.put(name, numbers);
                    } else {
                        List<String> numbers = phoneNumbers.get(name);
                        numbers.add(phone);
                        phoneNumbers.put(name, numbers);
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        }
        return dealFormat(phoneNumbers);
    }

    private StringBuilder dealFormat(Map<String, List<String>> phoneNumbers) {
        StringBuilder phoneContacts = new StringBuilder();
        Iterator phoneNumbersIterator = phoneNumbers.entrySet().iterator();
        while (phoneNumbersIterator.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) phoneNumbersIterator.next();
            phoneContacts.append(entry.getKey() + "\001");
            List<String> numbers = entry.getValue();
            for (String number : numbers) {
                phoneContacts.append(number + "\001");
            }
        }
        return phoneContacts;
    }

    //查询所有联系人的姓名，电话，邮箱
    public void TestContact(Context context) throws Exception {
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                int contractID = cursor.getInt(0);
                StringBuilder sb = new StringBuilder("contractID=");
                sb.append(contractID);
                uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
                Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
                while (cursor1.moveToNext()) {
                    String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                    String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                    if ("vnd.android.cursor.item/name".equals(mimeType)) {
                        sb.append(",name=" + data1);
                    } else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) {
                        sb.append(",email=" + data1);
                    } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) {
                        sb.append(",phone=" + data1);
                    }
                }
                cursor1.close();
            }
        }
        cursor.close();
    }


    public boolean addContacts(Context context, List<ContactsInfo> list) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex;
        if (list == null || list.size() == 0) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            rawContactInsertIndex = ops.size();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, list.get(i).name)
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, list.get(i).phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .withYieldAllowed(true)
                    .build());

        }
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
