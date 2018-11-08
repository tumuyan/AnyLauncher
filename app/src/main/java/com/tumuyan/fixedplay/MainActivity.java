package com.tumuyan.fixedplay;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.tumuyan.fixedplay.App.SelectOne;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends Activity {

    boolean v_up=false;
    boolean v_down=false;
    PackageManager packageManager ;
    final String THIS_PACKAGE="com.tumuyan.fixedplay";
    String mode="r2",action="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        packageManager=getPackageManager();
        Log.w("MainActivity","Create");
   //     go();
        super.onCreate(savedInstanceState);
      /*         setContentView(R.layout.activity_main);

            switch (mode){
                case "r2":
                    findViewById(R.id.r2).setSelected(true);
                    break;
                case "r1":
                    findViewById(R.id.r1).setSelected(true);
                    break;
            }


            findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent select=new Intent(MainActivity.this, SelectOne.class);
                    select.putExtra("mode",1);
                    startActivityForResult(select,1);
                }
            });

            findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent=new Intent();
                    intent.setClassName("com.android.settings",
                            "com.android.settings.applications.DefaultAppSelectionActivity");
                    startActivity(intent);

                 try
                    {
                         Intent intent=new Intent(Intent.ACTION_MAIN);
                         intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn=new ComponentName("com.android.settings",
                                "com.android.settings.applications.DefaultAppSelectionActivity");
                        intent.setComponent(cn);
                        startActivity(intent);
                    }catch (Exception e){
                     }
                    Toast.makeText(MainActivity.this,"这个功能看可能并没什么卵用，\n还是手动在系统里设置吧\n有解决问题的demo请联系我",Toast.LENGTH_LONG).show();

                Intent  paramIntent = new Intent("android.intent.action.MAIN");
                    paramIntent.setComponent(new ComponentName("android", "com.android.internal.app.ResolverActivity"));
                    paramIntent.addCategory("android.intent.category.DEFAULT");
                    paramIntent.addCategory("android.intent.category.HOME");
                    startActivity(paramIntent);

                    Intent intentw = new Intent(Intent.ACTION_MAIN);
                    intentw.addCategory(Intent.CATEGORY_HOME);
                    intentw.setClassName("android",
                            "com.android.internal.app.ResolverActivity");
                    startActivity(intentw);

                    IntentFilter filter = new IntentFilter();
                    filter.addAction("android.intent.action.MAIN");
                    filter.addCategory("android.intent.category.HOME");
                    filter.addCategory("android.intent.category.DEFAULT");
                    Context context = getApplicationContext();
                    ComponentName component = new ComponentName(context.getPackageName(), MainActivity.class.getName());
                    ComponentName[] components = new ComponentName[] {new ComponentName("com.example.launcher", "com.example.launcher.Launcher"), component};
                    packageManager.clearPackagePreferredActivities("com.example.launcher");
                    packageManager.addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, components, component);


                    Intent select=new Intent(MainActivity.this, com.android.settings.applications.DefaultAppSelectionActivity.class);
                    startActivity(select);
                }
            });
        */
    }
    @Override
    public void onStart(){
        Log.w("MainActivity","Start");
    //    go();
        super.onStart();
    }
    @Override
    public void onResume(){
        Log.w("MainActivity","Resume");
        go();
        super.onResume();
    }

    public void go(){
        SharedPreferences read = getSharedPreferences("setting",MODE_MULTI_PROCESS);
        String app = read.getString("app", "");
        String claseName=read.getString("class","");
        String uri = read.getString("uri","");
        mode=read.getString("mode","r2");
        action=read.getString("action","");
        Log.w("MainActivity mode" ,mode+"\n packagename: "+app);

        if (app.length()>0 && app!=THIS_PACKAGE){
            switch (mode){
                case "r2":
                {
                   Log.w("MainActivity mode2" ,mode);
                    Intent intent = packageManager.getLaunchIntentForPackage(app);
                    if (intent != null) startActivity(intent);
                    break;
                }

                case "r1":
                    /*                    */
                    if(claseName.length()>5)
                    {
                        Intent intent=new Intent();
                        intent.setClassName(app,claseName);
                        startActivity(intent);
                    }else {
                        //   Log.w("MainActivity mode1" ,mode);
                        Intent intent = new Intent();
                        intent = packageManager.getLaunchIntentForPackage(app);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
                        this.startActivity(intent);
                    }break;
                case "beta":
                    /*                    */{



                    Intent intent=new Intent();

                    if(action.length()>0 && !"none".equals(action)){
                        intent.setAction(action);
                    }

                    if(claseName.length()>5)
                    {
                        intent.setClassName(app,claseName);

                    }else {
                        intent = packageManager.getLaunchIntentForPackage(app);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;

                    }
                    try{
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this,"模式"+mode + "启动应用时发生了错误",Toast.LENGTH_SHORT).show();
                         intent=new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intent);

                    }


                }



                    break;

                case "uri":{
                    Uri u = Uri.parse(uri);
                    Intent intent = new Intent(Intent.ACTION_VIEW,u);


                    if(claseName.length()>0){
                        intent.setClassName(app,claseName);
                    }else{
                        intent.setPackage(app);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    break;
                }

/*   似乎没用*/
                case "uri_dail":{
                    Uri u = Uri.parse(uri);
                    Intent intent = new Intent(Intent.ACTION_DIAL, u);
                    if(claseName.length()>0){
                        intent.setClassName(app,claseName);
                    }else{
                        intent.setPackage(app);
                    }
                    startActivity(intent);
                    break;
                }

                case "uri_file":{
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(claseName.length()>0){
                        intent.setClassName(app,claseName);
                    }else{
                        intent.setPackage(app);
                    }
                    Uri u = Uri.fromFile(new File(uri));
                    intent.setDataAndType(u, "*/*");
                    startActivity(intent);

                    break;
                }


            }

        }
        else
        {
            Intent intent=new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);
        }
    }

/*    public void setSetting(View v){
        SharedPreferences.Editor editor = getSharedPreferences("setting", MODE_MULTI_PROCESS).edit();
        switch (v.getId()){
            case R.id.r1:
                editor.putString("mode", "r1");
                break;
            case R.id.r2:
                editor.putString("mode", "r2");
                break;
            case R.id.r3:
                editor.putString("mode", "r3");
                break;
        }
        editor.commit();
    }*/
   //禁止使用返回键返回到上一页,但是可以直接退出程序
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
           // moveTaskToBack(true);
            return true;//不执行父类点击事件
        }
         return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

/*    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
         if(keyCode==KeyEvent.KEYCODE_VOLUME_UP) {
            v_up=false;
            return super.onKeyDown(keyCode, event);
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && v_up) {
            {
                Intent select=new Intent(MainActivity.this, SelectOne.class);
                select.putExtra("mode",1);
                startActivityForResult(select,1);
            }
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP) {
            v_up=true;
            return super.onKeyDown(keyCode, event);
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         switch (requestCode) {
             case 1:
                 Toast.makeText(MainActivity.this,
                              "这是从第一个activity返回的数据---->>" + data.getStringExtra("text"),
                              Toast.LENGTH_LONG).show();

              break;
             case 2:
              Toast.makeText(MainActivity.this,
                              "这是从第二个activity返回的数据---->>" + data.getStringExtra("text"),
                              Toast.LENGTH_LONG).show();

                 break;
             case 0:
                 Toast.makeText(MainActivity.this,
                         "没有选中" + data.getStringExtra("text"),
                         Toast.LENGTH_LONG).show();
                 break;

             }

     }*/



}




