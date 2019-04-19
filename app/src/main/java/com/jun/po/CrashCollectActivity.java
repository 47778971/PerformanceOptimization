package com.jun.po;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class CrashCollectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialog();
    }

    private void showDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("检测到应用异常退出");
            builder.setMessage("是否修复? ");
            builder.setIcon(R.mipmap.ic_launcher_round);
            builder.setCancelable(false);
            builder.setPositiveButton("修复", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("重新启动", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(CrashCollectActivity.this, MainActivity.class);
                    CrashCollectActivity.this.startActivity(intent);
                    finish();
                }
            });
            builder.setNeutralButton("不做处理", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}