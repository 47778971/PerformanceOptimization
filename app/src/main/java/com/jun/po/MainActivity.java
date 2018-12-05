package com.jun.po;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jun.po.model.ContactsInfo;
import com.jun.po.util.ContactsHelper;
import com.jun.po.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ContactsInfo> allContacts = new ArrayList<>();
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                allContacts.addAll(ContactsHelper.getInstance().getPhoneContacts(MainActivity.this));
                allContacts.addAll(ContactsHelper.getInstance().getSimContacts(MainActivity.this));
                Log.i("allContacts", allContacts.toString());
                StringBuilder contacts = new StringBuilder();
                for (int i = 0; i < allContacts.size(); i++) {
                    contacts.append("姓名：" + allContacts.get(i).name + "   手机：" + allContacts.get(i).phone + "\n\r");
                }
                LogUtil.contactsInfo2File(contacts.toString());
            }
        }).start();

        for (int i = 0; i < 100; i++) {
            TextView view = new TextView(this);
            view.setText(i + "" + i + "" + i + "" + i + "" + i);
            view.setTextColor(Color.BLACK);
            view.setHeight(200);
            view.setGravity(Gravity.CENTER);
            view.setTextSize(16);
            layout.addView(view);
        }

//        for (int i = 0; i<300;i++){
//            ContactsInfo contactsInfo = new ContactsInfo();
//            contactsInfo.name = "张三" + i;
//            contactsInfo.phone = "19876543210";
//            allContacts.add(contactsInfo);
//        }
//        ContactsHelper.getInstance().addContacts(this, allContacts);
    }
}