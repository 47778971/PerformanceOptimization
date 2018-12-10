package com.jun.po;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
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
                LogUtil.contactsInfo2File(ContactsHelper.getInstance().getContacts(MainActivity.this));
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
                Toast.makeText(MainActivity.this, "button2", Toast.LENGTH_LONG).show();
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
        DeviceUtil.getDeviceInfo();
    }
}