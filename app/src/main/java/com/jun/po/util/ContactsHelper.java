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
import java.util.List;
import java.util.Map;

public class ContactsHelper {

    private static ContactsHelper contactsHelper;

    // 手机联系人信息
    Map<String, List<String>> phoneNumbers = new HashMap();
    List<ContactsInfo> phoneContacts = new ArrayList<>();
    List<ContactsInfo> simContacts = new ArrayList<>();
    ContactsInfo phoneContactsInfo;
    ContactsInfo simContactsInfo;

    private static final String _ID = ContactsContract.Contacts._ID;
    private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String CONTACT_ID = ContactsContract.Data.CONTACT_ID;

    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
    private static final String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;
    private static final String EMAIL_TYPE = ContactsContract.CommonDataKinds.Email.TYPE;

    public static ContactsHelper getInstance() {
        if (null == contactsHelper) {
            contactsHelper = new ContactsHelper();
        }
        return contactsHelper;
    }

    private ContactsHelper() {
    }

    public List<ContactsInfo> getPhoneContacts(Context context) {
        Cursor cursor = null;
        try {
            ContentResolver cr = context.getContentResolver();
            String str[] = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_ID};
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, str, null, null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    phoneContactsInfo = new ContactsInfo();
                    phoneContactsInfo.phone = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
                    phoneContactsInfo.phone = phoneContactsInfo.phone.replace(" ", "");
                    phoneContactsInfo.name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    phoneContactsInfo.email = cursor.getString(cursor.getColumnIndex(EMAIL_DATA));
                    if (phoneNumbers.get(phoneContactsInfo.name) == null) {
                        List<String> numbers = new ArrayList<>();
                        numbers.add(phoneContactsInfo.phone);
                        phoneNumbers.put(phoneContactsInfo.name, numbers);
                    } else {
                        if (phoneNumbers.get(phoneContactsInfo.name).size() >= 3) { //判断一个名字存在多于3个号码，则显示最多3个号码
                            continue;
                        } else {
                            List<String> numbers = phoneNumbers.get(phoneContactsInfo.name);
                            numbers.add(phoneContactsInfo.phone);
                            phoneNumbers.put(phoneContactsInfo.name, numbers);
                        }
                    }
                    phoneContacts.add(phoneContactsInfo);
                }
                if (null != cursor) cursor.close();
                cursor = null;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) cursor.close();
        }
        return phoneContacts;
    }

    public List<ContactsInfo> getSimContacts(Context context) {
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://icc/adn");
            cursor = contentResolver.query(uri, null, null, null, null);
            if (null != cursor) {
                while (cursor.moveToFirst()) {
                    simContactsInfo = new ContactsInfo();
                    simContactsInfo.name = cursor.getString(cursor.getColumnIndex("name"));
                    simContactsInfo.phone = cursor.getString(cursor.getColumnIndex("number"));
                    simContactsInfo.phone = simContactsInfo.phone.replace(" ", "");
                    if (phoneNumbers.get(simContactsInfo.phone) == null) {
                        List<String> numbers = new ArrayList<String>();
                        numbers.add(simContactsInfo.phone);
                        phoneNumbers.put(simContactsInfo.name, numbers);
                    } else {
                        if (phoneNumbers.get(simContactsInfo.name).size() >= 3) {//判断一个名字存在多于3个号码，则显示最多3个号码
                            continue;
                        } else {
                            List<String> numbers = phoneNumbers.get(simContactsInfo.name);
                            numbers.add(simContactsInfo.phone);
                            phoneNumbers.put(simContactsInfo.name, numbers);
                        }
                    }
                    simContacts.add(simContactsInfo);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) cursor.close();
        }
        return simContacts;
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
