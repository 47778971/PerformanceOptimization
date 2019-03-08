package com.jun.po;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jun.po.util.ContactsHelper;
import com.jun.po.util.DeviceUtil;
import com.jun.po.util.LogUtil;
import com.jun.po.util.MyIntentService;
import com.jun.po.util.SnowFlake;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        testGlideHook();
    }

    private void initView() {
        imageView = findViewById(R.id.image_view);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
    }

    private void testContacts() {
        final HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    String line1Number = tm.getLine1Number();
                }
                LogUtil.contactsInfo2File(ContactsHelper.getInstance().getContacts(MainActivity.this));
                handlerThread.quit();
            }
        };
        handler.sendEmptyMessage(0);
//        List<ContactsInfo> list = new ArrayList<>();
//        for (int i = 1501; i <= 1999; i++) {
//            ContactsInfo contactsInfo = new ContactsInfo();
//            contactsInfo.name = "张" + i;
//            contactsInfo.phone = String.valueOf(12576300000L + i);
//            list.add(contactsInfo);
//        }
//        ContactsHelper.getInstance().addContacts(MainActivity.this, list);
    }

    private void testCrash() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nullString = null;
                nullString.length();
            }
        });
    }

    private void testANR() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                while (true) {
                    Log.i("hahaha", "testANR");
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "button2", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void testGlideHook() {
        Glide.with(this).load("http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg").into(imageView);

//        Glide.with(this).load("http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg").into(new SimpleTarget<GlideDrawable>() {
//            @Override
//            public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
//                imageView.setImageDrawable(resource);
//            }
//        });
    }

    private void testImageLoaderHook() {
        ImageLoader.getInstance().displayImage("http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg", imageView);
    }

    private void testSnowFlake() {
        SnowFlake snowFlake = new SnowFlake(0, 0);
        for (long i = 0; i < 100; i++) {
            Long id = snowFlake.nextId();
            Log.i("SnowFlake", "id:" + id);
        }
    }

    private void testDeviceInfo() {
        DeviceUtil.getDeviceInfo(this);
    }
}