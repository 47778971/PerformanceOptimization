package com.jun.po;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jun.po.model.ContactsInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ContactsInfo> allContacts = new ArrayList<>();
    private Button button1;
    private Button button2;

//    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

//        imageView = findViewById(R.id.image_view);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                allContacts.addAll(ContactsHelper.getInstance().getPhoneContacts(MainActivity.this));
//                allContacts.addAll(ContactsHelper.getInstance().getSimContacts(MainActivity.this));
//                LogUtil.contactsInfo2File(new Gson().toJson(allContacts));
//            }
//        }).start();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("com.jun.po.MyBroadcastAction");
//                sendBroadcast(intent);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("MyBroadcastReceiver", "onReceive");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "button2", Toast.LENGTH_LONG).show();
            }
        });


//        for (int i = 0; i<300;i++){
//            ContactsInfo contactsInfo = new ContactsInfo();
//            contactsInfo.name = "张三" + i;
//            contactsInfo.phone = "19876543210";
//            allContacts.add(contactsInfo);
//        }
//        ContactsHelper.getInstance().addContacts(this, allContacts);

//        Glide.with(this).load("http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg").into(imageView);

//        loadImage();

//        ANRUtils.catchAnr(this);
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