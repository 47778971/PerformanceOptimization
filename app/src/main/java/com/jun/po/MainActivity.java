package com.jun.po;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.jun.po.aspectjx.ITrack;
import com.jun.po.model.ContactsInfo;
import com.jun.po.util.ANRUtils;
import com.jun.po.util.ContactsHelper;
import com.jun.po.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ContactsInfo> allContacts = new ArrayList<>();
    LinearLayout layout;

//    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout);
//        imageView = findViewById(R.id.image_view);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                allContacts.addAll(ContactsHelper.getInstance().getPhoneContacts(MainActivity.this));
//                allContacts.addAll(ContactsHelper.getInstance().getSimContacts(MainActivity.this));
//                LogUtil.contactsInfo2File(new Gson().toJson(allContacts));
//            }
//        }).start();

        for (int i = 0; i < 100; i++) {
            TextView view = new TextView(this);
            view.setText(i + "" + i + "" + i + "" + i + "" + i);
            view.setTextColor(Color.BLACK);
            view.setHeight(200);
            view.setGravity(Gravity.CENTER);
            view.setTextSize(16);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            layout.addView(view);
        }

//        for (int i = 0; i<300;i++){
//            ContactsInfo contactsInfo = new ContactsInfo();
//            contactsInfo.name = "张三" + i;
//            contactsInfo.phone = "19876543210";
//            allContacts.add(contactsInfo);
//        }
//        ContactsHelper.getInstance().addContacts(this, allContacts);

//        Glide.with(this).load("http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg").into(imageView);

//        loadImage();

        ANRUtils.catchAnr(this);
    }


    SimpleTarget<GlideDrawable> simpleTarget = new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
//            imageView.setImageDrawable(resource);
        }
    };

    public void loadImage() {
        Glide.with(this).load("http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg").into(simpleTarget);
    }
}