package com.tumuyan.fixedplay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.io.File;

public class MainActivity extends Activity {

    PackageManager packageManager;
    final String THIS_PACKAGE = "com.tumuyan.fixedplay";
    long splash_time = 0;
    String mode = "r2", action = "";
    ImageView imgview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        packageManager = getPackageManager();
        Log.w("MainActivity", "Create");

        SharedPreferences read = getSharedPreferences("setting", MODE_MULTI_PROCESS);
        final String splash_img = read.getString("splash_img", "");

        splash_time = read.getInt("splash_time", 0);

        Log.w("MainActivity", "Create, splash_time = " + splash_time);
//        if (System.currentTimeMillis() - SystemClock.elapsedRealtime() >60000 ){
//            splash_time =0;
//        }
        if (splash_time > 0) {
            handler.sendEmptyMessageDelayed(GO, splash_time);
            setContentView(R.layout.splash_activity);
            imgview = findViewById(R.id.splash_img);
            imgview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skip_splash();
                }
            });
            Glide.with(this)
                    .load(splash_img)
                    .placeholder(R.drawable.ic_baseline_hourglass_top_24)
//                    .asGif()
                    .into(imgview);
        }

        super.onCreate(savedInstanceState);
    }

    private void skip_splash() {
        if (imgview != null) {
            handler.removeMessages(GO);
            splash_time = 0;
            imgview.setImageDrawable(null);
            go();
        }
    }

    @Override
    public void onStart() {
        Log.w("MainActivity", "Start");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.w("MainActivity", String.format("Resume, splash %d", splash_time));
        if (splash_time <= 0)
            go();
        super.onResume();
    }


    public void go() {
        SharedPreferences read = getSharedPreferences("setting", MODE_MULTI_PROCESS);
        String app = read.getString("app", "");
        String claseName = read.getString("class", "");
        String uri = read.getString("uri", "");
        mode = read.getString("mode", "r2");
        action = read.getString("action", "");
        Log.i("MainActivity.go()", "mode=" + mode + ", packagename=" + app);

        boolean apply2nd = read.getBoolean("apply2nd", false);
        long lastTime = read.getLong("lastTime", 0);
        int combo = read.getInt("combo", 0);

        final String app_2nd = read.getString("app_2nd", "");
        final String class_2nd = read.getString("class_2nd", "");
        Log.w("2nd", app_2nd + ", combo=" + combo);

        if (apply2nd) {

            long time = System.currentTimeMillis();
            if (combo > 3)
                combo = 0;
            else {
                if (time - lastTime < 500) {
                    combo++;
                } else {
                    combo = 0;
                }
            }

            Log.w("combo", "" + combo);
            {
                SharedPreferences.Editor editor = getSharedPreferences("setting", MODE_MULTI_PROCESS).edit();
                editor.putInt("combo", combo);
                editor.putLong("lastTime", time);
                editor.commit();
            }
            if (combo > 1) {
                if (app_2nd.length() > 0) {
                    Intent intent = new Intent();

                    intent = packageManager.getLaunchIntentForPackage(app_2nd);
                    if (intent != null) {
                        intent.addCategory(Intent.CATEGORY_HOME);
                        Log.w("2nd2", "length>0 -> intent not null");
                        startActivity(intent);
                    } else {
                        // Toast.makeText(SettingActivity.this,R.string.error_could_not_start,Toast.LENGTH_SHORT).show();

                        intent = new Intent();
                        intent.setAction(Intent.ACTION_MAIN);
                        if (class_2nd.length() > 5) {
                            intent.setClassName(app_2nd, class_2nd);
                        }
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("startActivity", app_2nd + ", " + class_2nd);
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, R.string.error_could_not_start, Toast.LENGTH_SHORT).show();
                            intent = new Intent(MainActivity.this, SettingActivity.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
                return;
            }
        }

        if (app.length() > 0 && app != THIS_PACKAGE) {
            switch (mode) {
                case "r2": {
                    Log.w("MainActivity mode2", mode);
                    Intent intent = packageManager.getLaunchIntentForPackage(app);
                    if (intent != null) startActivity(intent);
                    break;
                }

                case "r1":
                    /*                    */
                    if (claseName.length() > 5) {
                        Intent intent = new Intent();
                        intent.setClassName(app, claseName);
                        startActivity(intent);
                    } else {
                        //   Log.w("MainActivity mode1" ,mode);
                        Intent intent = new Intent();
                        intent = packageManager.getLaunchIntentForPackage(app);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        this.startActivity(intent);
                    }
                    break;
                case "beta":
                    /*                    */
                {


                    Intent intent = new Intent();

                    if (action.length() > 0 && !"none".equals(action)) {
                        intent.setAction(action);
                    }

                    if (claseName.length() > 5) {
                        intent.setClassName(app, claseName);

                    } else {
                        intent = packageManager.getLaunchIntentForPackage(app);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    }
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //   Toast.makeText(this,getString(R.string.toast_main_start_error,mode)"模式"+mode + "启动应用时发生了错误",Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, getString(R.string.toast_main_start_error, mode), Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);

                    }


                }


                break;

                case "uri": {
                    Uri u = Uri.parse(uri);
                    Intent intent = new Intent(Intent.ACTION_VIEW, u);


                    if (claseName.length() > 0) {
                        intent.setClassName(app, claseName);
                    } else {
                        intent.setPackage(app);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    break;
                }

                /*   似乎没用*/
                case "uri_dail": {
                    Uri u = Uri.parse(uri);
                    Intent intent = new Intent(Intent.ACTION_DIAL, u);
                    if (claseName.length() > 0) {
                        intent.setClassName(app, claseName);
                    } else {
                        intent.setPackage(app);
                    }
                    startActivity(intent);
                    break;
                }

                case "uri_file": {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (claseName.length() > 0) {
                        intent.setClassName(app, claseName);
                    } else {
                        intent.setPackage(app);
                    }
                    Uri u = Uri.fromFile(new File(uri));
                    intent.setDataAndType(u, "*/*");
                    startActivity(intent);

                    break;
                }


            }

        } else {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //禁止使用返回键返回到上一页,但是可以直接退出程序
            return true;//不执行父类点击事件
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//            跳过
            skip_splash();
            return true;
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    private static final int GO = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO:
                    go();
                    splash_time = 0;
                    break;
            }
        }
    };
}




