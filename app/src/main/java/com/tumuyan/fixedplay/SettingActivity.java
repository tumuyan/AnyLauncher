package com.tumuyan.fixedplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tumuyan.fixedplay.App.SelectOne;
import com.tumuyan.fixedplay.Beta.SelectApp;

public class SettingActivity extends Activity {
    PackageManager packageManager;

    String mode = "r2";

    String _mode = "";
    String _uri = "";
    String _action = "";
    String _data = "";

    int _intent_type = 0;

    TextView Text_PackageName;
    private View app_view_2nd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        packageManager = getPackageManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Text_PackageName = (TextView) findViewById(R.id.text_pakageName);
        app_view_2nd = (View) findViewById(R.id.app_view_2nd);

        go();
        go2ndLauncher();
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] items3 = getResources().getStringArray(R.array.mode_main);
//                final String[] items3 = new String[]{"优先从后台切换到前台", "重新打开应用Activity", "浏览网页", "打开地图","拨打电话","打开文件","特定Activity","快捷方式","备用启动器"};//创建item
                AlertDialog alertDialog3 = new AlertDialog.Builder(SettingActivity.this)
                        //  .setTitle("选择任务模式")
                        //   .setIcon(R.mipmap.ic_launcher)
                        .setItems(items3, new DialogInterface.OnClickListener() {//添加列表
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                _mode = "";
                                _uri = "";
                                _action = "";
                                _data = "";
                                _intent_type = i;
                                Intent select = new Intent(SettingActivity.this, SelectOne.class);
                                switch (i) {
                                    case 0:
                                        _mode = "r2";
                                        select.putExtra("_mode", _mode);
                                        startActivity(select);
                                        break;
                                    case 1:
                                        _mode = "r1";
                                        select.putExtra("_mode", _mode);
                                        startActivity(select);
                                        break;

                                    case 2:
                                        _mode = "uri";
                                        inputUri(getString(R.string.title_hint_url), "http://m.baidu.com", "");
                                        break;
                                    case 3:
                                        _mode = "uri";
                                        inputUri(getString(R.string.title_hint_geo), "geo:38.899533,-77.036476", "geo:");
                                        break;
                                    case 4:
                                        //    _mode="uri";
                                        _mode = "uri_dail";
                                        inputUri(getString(R.string.title_hint_tel), "tel:10086", "tel:");
                                        break;

                                    case 5:
                                        _mode = "uri_file";
                                        inputUri(getString(R.string.title_hint_file), "/sdcard/logs.txt", "");
                                        break;
                                    case 6:
                                        Intent i6 = new Intent(SettingActivity.this, SelectApp.class);
                                        startActivity(i6);
                                        break;
                                    case 8:
                                        select.putExtra("_mode", "2nd");
                                        startActivity(select);
                                        break;
                                    case 7:
                                        _mode = "short_cut";
                                        select.putExtra("_mode", _mode);
                                        startActivity(select);
                                        break;

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

                Intent intent = new Intent();
                intent.setClassName("com.android.settings",
                        "com.android.settings.applications.DefaultAppSelectionActivity");
                intent.setAction("android.settings.HOME_SETTINGS");

                if (null == packageManager.resolveActivity(intent, 0)) {
                    intent.setClassName("com.android.settings",
                            "com.android.settings.Settings$AdvancedAppsActivity");
                    Log.w("button1", "DefaultHomeSettings");
                }


/*                if(null==packageManager.resolveActivity(intent, 0)){
                    intent = new Intent("com.miui.settings.HOME_SETTINGS_MIUI");
                    intent.setClassName("com.android.settings",
                            "com.android.settings.applications.DefaultHomeSettings");
//                    intent.setAction("com.miui.settings.HOME_SETTINGS_MIUI");
//                    miui.permission.USE_INTERNAL_GENERAL_API

                    Log.w("button1","DefaultHomeSettings");
                }*/

                if (null != packageManager.resolveActivity(intent, 0)) {
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.w("button1", "creash");
                        e.printStackTrace();
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addCategory(Intent.CATEGORY_HOME);
                        startActivity(i);
                    }
                } else {
                    Log.w("button1", "not find");
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addCategory(Intent.CATEGORY_HOME);
                    startActivity(i);
                }
            }
        });

    }


    public void inputUri(String title, String hint, final String prefix) {
        final EditText inputServer = new EditText(SettingActivity.this);
        inputServer.setHint(hint);
        final String fix;
        if (prefix == null) {
            fix = "";
        } else {
            fix = prefix.toLowerCase();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle(title).
                setView(inputServer).
                setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _uri = inputServer.getText().toString().toLowerCase();
                        Log.w("_uri", _uri);
                        if (_uri.length() > 0) {
                            String _sub_uri = _uri.substring(0, fix.length());
                            if (_sub_uri.equals(prefix)) {
                                _uri = fix + _uri.substring(fix.length());
                            } else {
                                _uri = fix + _uri;
                            }

                            Intent select = new Intent(SettingActivity.this, SelectOne.class);
                            select.putExtra("_mode", _mode);
                            select.putExtra("_uri", _uri);
                            startActivity(select);
                        }
                    }
                }
        );
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        go();
        go2ndLauncher();
    }

    public void go() {
        SharedPreferences read = getSharedPreferences("setting", MODE_MULTI_PROCESS);

        final String app = read.getString("app", "");
        if (app.length() < 1) return;

        mode = read.getString("mode", "r2");
        final String label = read.getString("label", "");
        final String uri = read.getString("uri", "");
        final String className = read.getString("class", "");

        final Drawable icon;

        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(app, 0);
            icon = (appInfo.loadIcon(packageManager));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Text_PackageName.setText(label);
                    //耗时操作，需要在子线程中完成操作后通知主线程实现UI更新
                    String desc;
                    if (uri.length() > 0) {
                        desc = className + "\n" + uri;
                    } else {
                        desc = className;
                    }

                    ((ImageView) findViewById(R.id.item_img)).setImageDrawable(icon);
                    ((TextView) findViewById(R.id.item_text)).setText(app);
                    ((TextView) findViewById(R.id.item_packageName)).setText(desc);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void go2ndLauncher() {
        SharedPreferences read = getSharedPreferences("setting", MODE_MULTI_PROCESS);
        boolean apply2nd = read.getBoolean("apply2nd", false);
        CheckBox cbx2ndLauncher = ((CheckBox) findViewById(R.id.cbx_2nd_launcher));
        cbx2ndLauncher.setChecked(apply2nd);
        cbx2ndLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = ((CheckBox) view).isChecked();
                SharedPreferences.Editor editor = SettingActivity.this.getSharedPreferences("setting", MODE_MULTI_PROCESS).edit();
                editor.putBoolean("apply2nd", state);
                editor.commit();
            }
        });

        final String app = read.getString("app_2nd", "");
        if (app.length() < 1) return;

        final Drawable icon;
        final String label_2nd = read.getString("label_2nd", "");
        final String class_2nd = read.getString("class_2nd", "");
        Log.w("get 2nd", label_2nd + " - " + app + " - " + class_2nd);
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(app, 0);
            icon = (appInfo.loadIcon(packageManager));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) app_view_2nd.findViewById(R.id.item_img)).setImageDrawable(icon);
                    ((TextView) app_view_2nd.findViewById(R.id.item_text)).setText(label_2nd);
                    ((TextView) app_view_2nd.findViewById(R.id.item_packageName)).setText(app);
                    app_view_2nd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = packageManager.getLaunchIntentForPackage(app);

                            if (intent != null) {
                                //  intent.setAction(Intent.ACTION_MAIN);
                                //    intent.addCategory("android.intent.category.HOME" );
                                //   intent.setAction(Intent.ACTION_VIEW);
                                startActivity(intent);
                                Log.i( "go2ndLauncher","intent not null");
                            } else {
                                // Toast.makeText(SettingActivity.this,R.string.error_could_not_start,Toast.LENGTH_SHORT).show();

                                intent = new Intent();
                                intent.setAction(Intent.ACTION_MAIN);
                                if (class_2nd.length() > 5) {
                                    intent.setClassName(app, class_2nd);
                                }
                                try {
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(SettingActivity.this, R.string.error_could_not_start, Toast.LENGTH_SHORT).show();
                                }

                                Log.i( "go2ndLauncher","intent is null,"+intent);
                            }

                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean isPkgInstalled(String packageName) {
        if (null == packageName)
            return false;
        if ((packageName).length() < 0)
            return false;
        android.content.pm.ApplicationInfo info;
        try {
            info = packageManager.getApplicationInfo(packageName, 0);
            return null != info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
