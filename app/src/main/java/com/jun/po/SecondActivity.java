package com.jun.po;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jun.po.util.ContactsHelper;
import com.jun.po.util.DeviceUtil;
import com.jun.po.util.LogUtil;
import com.jun.po.util.SnowFlake;

public class SecondActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private ImageView imageView;
    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
        testANR();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                Log.i("ActivityManager", "onResume耗时：" + (System.currentTimeMillis() - startTime));
                return false;
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i("ActivityManager", "onPostResume耗时：" + (System.currentTimeMillis() - startTime));
    }

    private void initView() {
        imageView = findViewById(R.id.image_view);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
    }

    private void testContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TelephonyManager tm = (TelephonyManager) SecondActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    String line1Number = tm.getLine1Number();
                }
                LogUtil.contactsInfo2File(ContactsHelper.getInstance().getContacts(SecondActivity.this));
            }
        }).start();
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
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "button2", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void testGlideHook() {
//        Glide.with(this).load("http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg").into(imageView);

        Glide.with(this).load("http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg").into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
                imageView.setImageDrawable(resource);
            }
        });
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