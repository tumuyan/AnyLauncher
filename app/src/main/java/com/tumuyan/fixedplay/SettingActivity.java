package com.tumuyan.fixedplay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tumuyan.fixedplay.App.ItemAdapter;
import com.tumuyan.fixedplay.App.SelectOne;

public class SettingActivity extends Activity {
    PackageManager packageManager ;
    final String THIS_PACKAGE="com.tumuyan.fixedplay";
    String mode="r2";

    RadioButton R1,R2,R3;
    TextView Text_PackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        packageManager=getPackageManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        R2=(RadioButton) findViewById(R.id.r2) ;
        R1=(RadioButton) findViewById(R.id.r1) ;
        Text_PackageName=(TextView)findViewById(R.id.text_pakageName);

        go();



        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent select=new Intent(SettingActivity.this, SelectOne.class);
                select.putExtra("mode",1);
                startActivityForResult(select,1);
            }
        });

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    Intent intent=new Intent();
                    intent.setClassName("com.android.settings",
                            "com.android.settings.applications.DefaultAppSelectionActivity");
                    startActivity(intent);

                }catch (Exception e){
                    Toast.makeText(SettingActivity.this,R.string.error_btn1,Toast.LENGTH_LONG).show();
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

    @Override
    public void onResume(){
        super.onResume();
        go();
    }

    public void go(){
        SharedPreferences read = getSharedPreferences("setting",MODE_MULTI_PROCESS);
        final String app = read.getString("app", "");
        mode=read.getString("mode","r2");
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Text_PackageName.setText(app);
                //耗时操作，需要在子线程中完成操作后通知主线程实现UI更新
                switch (mode){
                    case "r2":
                        R2.setSelected(true);
                        R1.setSelected(false);
                        Log.i("set 2",mode);
                        break;
                    case "r1":
                        R1.setSelected(true);
                        R2.setSelected(false);
                        Log.i("set 1",mode);
                        break;
                }
            }

        });

    }

    public void setSetting(View v){
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

        go();
    }

}
