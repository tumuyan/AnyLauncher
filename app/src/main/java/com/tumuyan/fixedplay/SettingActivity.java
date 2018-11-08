package com.tumuyan.fixedplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tumuyan.fixedplay.App.ItemAdapter;
import com.tumuyan.fixedplay.App.SelectOne;
import com.tumuyan.fixedplay.Beta.SelectApp;

public class SettingActivity extends Activity {
    PackageManager packageManager ;
    final String THIS_PACKAGE="com.tumuyan.fixedplay";
    String mode="r2";

    String _mode="";
    String _uri="";
    String _action="";
    String _data="";

    int _intent_type=0;

    TextView Text_PackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        packageManager=getPackageManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Text_PackageName=(TextView)findViewById(R.id.text_pakageName);

        go();

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items3 = new String[]{"优先从后台切换到前台", "每次都重新打开应用Activity", "浏览网页", "打开地图","拨打电话","打开文件","高级模式"};//创建item
                AlertDialog alertDialog3 = new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("选择工作模式")
                     //   .setIcon(R.mipmap.ic_launcher)
                        .setItems(items3, new DialogInterface.OnClickListener() {//添加列表
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               // Toast.makeText(SettingActivity.this, "工作模式：" + items3[i], Toast.LENGTH_SHORT).show();

                                 _mode="";
                                 _uri="";
                                 _action="";
                                 _data="";
                                 _intent_type=i;
                                Intent select=new Intent(SettingActivity.this, SelectOne.class);
                                switch (i){
                                    case 0:
                                        _mode="r2";
                                        select.putExtra("_mode",_mode);
                                        startActivity(select);
                                        break;
                                    case 1:
                                        _mode="r1";
                                        select.putExtra("_mode",_mode);
                                        startActivity(select);
                                        break;

                                    case 2:
                                        _mode="uri";
                                        inputUri("网址链接","http://m.baidu.com","");
                                        break;
                                    case 3:
                                        _mode="uri";
                                        inputUri("地图坐标","geo:38.899533,-77.036476","geo:");
                                            break;
                                    case 4:
                                        //    _mode="uri";
                                     _mode="uri_dail";
                                        inputUri("电话号码","tel:10086","tel:");
                                        break;

                                    case 5:
                                        _mode="uri_file";
                                        inputUri("文件路径","/sdcard/logs.txt","");
                                        break;
                                    case 6:
                                        Intent i6=new Intent(SettingActivity.this,SelectApp.class);
                                        startActivity(i6);




                                }
                            }
                        })
                        .create();
                alertDialog3.show();
            }
        });

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    Intent intent=new Intent();
                    intent.setClassName("com.android.settings",
                            "com.android.settings.applications.DefaultAppSelectionActivity");
                    intent.setAction("android.settings.HOME_SETTINGS");
                    startActivity(intent);
                }catch (Exception e){
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addCategory(Intent.CATEGORY_HOME);
                    startActivity(i);
                 //   Toast.makeText(SettingActivity.this,R.string.error_btn1,Toast.LENGTH_LONG).show();
                }


/*                    try
                    {
                         Intent intent=new Intent(Intent.ACTION_MAIN);
                         intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn=new ComponentName("com.android.settings",
                                "com.android.settings.applications.DefaultAppSelectionActivity");
                        intent.setComponent(cn);
                        startActivity(intent);
                    }catch (Exception e){
                     }*/


            }
        });

    }


    public void  inputUri(String title, String hint, final String prefix){
        final EditText inputServer = new EditText(SettingActivity.this);
                        inputServer.setHint(hint);
        final String fix;
        if(prefix==null){
           fix="";
        }else{
            fix=prefix.toLowerCase();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle(title).
              //  setIcon(android.R.drawable.ic_dialog_info).
                setView(inputServer).
                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _uri = inputServer.getText().toString().toLowerCase();
                        Log.w("_uri",_uri);
                        if(_uri.length()>0) {
                            String _sub_uri=_uri.substring(0,fix.length());
                            if(_sub_uri.equals(prefix)){
                                _uri=fix+_uri.substring(fix.length());
                            }else{
                                _uri=fix+_uri;
                            }

                            Intent select=new Intent(SettingActivity.this, SelectOne.class);
                            select.putExtra("_mode",_mode);
                            select.putExtra("_uri",_uri);
                            startActivity(select);
                        }

                    }
                }
        );
        builder.show();
    }

    @Override
    public void onResume(){
        super.onResume();
        go();
    }

    public void go(){
        SharedPreferences read = getSharedPreferences("setting",MODE_MULTI_PROCESS);
        final String app = read.getString("app", "");
        if(app.length()<1) return;

        mode=read.getString("mode","r2");
        final String label=read.getString("label","");
        final String uri=read.getString("uri","");
        final String className=read.getString("class","");
        final  Drawable icon ;
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(app, 0);
            icon=(appInfo.loadIcon(packageManager));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Text_PackageName.setText(label);
                    //耗时操作，需要在子线程中完成操作后通知主线程实现UI更新
                    String desc;
                    if(uri.length()>0){
                          desc=className+"\n"+uri;
                    }else{
                          desc=className;
                    }

                    ((ImageView) findViewById(R.id.item_img)).setImageDrawable(icon);
                    ((TextView) findViewById(R.id.item_text)).setText(app);
                    ((TextView) findViewById(R.id.item_packageName)).setText(desc);
                }

            });
        }catch (Exception e){
            e.printStackTrace();
            //icon=getResources().getDrawable(R.mipmap.ic_launcher);
        }
 /*        String className=read.getString("class","");
        if(className.length()>0){
           icon  =pm.getActivityIcon(ComponentName.createRelative(app,className));
        }
      */




    }


}
