package com.tumuyan.fixedplay.App;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tumuyan.fixedplay.MainActivity;
import com.tumuyan.fixedplay.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SelectOne extends Activity {
    // Adapter显示项目（筛选后
    private List<Item> list = new ArrayList<>();
    // 原始搜索结果
    private List<Item> listOrgin = new ArrayList<>();

    private ListView listView;
    private ProgressBar progressBar;
    private String _mode, _action, _uri;

    private View SearchBox;
    private EditText SearchText;
    private Button SearchButton;
    ItemAdapter itemAdapter;
    private Drawable defaultIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist_activity);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        SearchBox = (View) findViewById(R.id.searchBox);
        SearchButton = (Button) findViewById(R.id.button_filter);
        SearchText = (EditText) findViewById(R.id.editText_filter);
        SearchBox.setVisibility(View.GONE);
        Intent intent = getIntent();
        _mode = intent.getStringExtra("_mode");
        _action = intent.getStringExtra("_action");
        _uri = intent.getStringExtra("_uri");
        defaultIcon = getResources().getDrawable(R.drawable.unknow);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //      getAppList();
                loadAllApps(makeIntent());
                list.addAll(listOrgin);

                itemAdapter = new ItemAdapter(SelectOne.this, R.layout.applist_item, list);
                itemAdapter.setMode(_mode);
                itemAdapter.setUri(_uri);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //耗时操作，需要在子线程中完成操作后通知主线程实现UI更新
                        listView = (ListView) findViewById(R.id.listview);
                        if (listView == null) Log.e("listitem", "null");
                        listView.setAdapter(itemAdapter);
                        progressBar.setVisibility(View.GONE);
                        SearchBox.setVisibility(View.VISIBLE);


                        listView.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (event.getAction() == KeyEvent.ACTION_UP)
                                {

                                    if ( keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

                                        int selectedItemPosition = listView.getSelectedItemPosition();
                                        Log.i("App SelectorOne", "Keycode = " + keyCode + ", cursorPosition = " + selectedItemPosition);
                                        Item item = list.get(selectedItemPosition);
                                        itemAdapter.select(item.getName(), item.getPackageName(), item.getClassName() );
                                        return true;
                                    }
                                }
                                return false;
                            }
                        });

                    }

                });

            }

        }).start();

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch(SearchText.getText().toString());
            }
        });
    }

//
//    class searchWatcher implements TextWatcher {
//        @Override
//        public void afterTextChanged(Editable s) {//表示最终内容 Log.d("afterTextChanged", s.toString());
//             }
//            // /** * * @param s * @param start 开始的位置 * @param count 被改变的旧内容数 * @param after 改变后的内容数量 */
//            @Override
//            public void beforeTextChanged (CharSequence s,int start, int count, int after)
//            { //这里的s表示改变之前的内容，通常start和count组合，可以在s中读取本次改变字段中被改变的内容。而after表示改变后新的内容的数量。
//                 }
//                 /** * * @param s * @param start 开始位置 * @param before 改变前的内容数量 * @param count 新增数 */
//                 @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                     //这里的s表示改变之后的内容，通常start和count组合，可以在s中读取本次改变字段中新的内容。而before表示被改变的内容的数量。
//                doSearch(s); }
//
//            }

    private void doSearch(String str) {
        list.clear();
        for (int i = 0; i < listOrgin.size(); i++) {
            Item item = listOrgin.get(i);
            if (item.getClassName().contains(str) ||
                    item.getPackageName().contains(str) ||
                    item.getName().contains(str)) {
                list.add(item);
            }
        }
        Log.w("search", str + " get" + list.size());
        itemAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
    // 停用的旧方法
    private void getAppList() {
        PackageManager pm = getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
/*
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                System.out.println("MainActivity.getAppList, packageInfo=" + packageInfo.packageName);
            } else {
                // 系统应用
            }
*/

            Item item = new Item(
                    pm.getApplicationLabel(packageInfo.applicationInfo).toString(),
                    packageInfo.applicationInfo.packageName,
                    pm.getApplicationIcon(packageInfo.applicationInfo)
            );

            list.add(item);

        }

    }


    private void loadAllApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> mApps;
        mApps = new ArrayList<>();
        mApps.addAll(this.getPackageManager().queryIntentActivities(mainIntent, 0));


/*        包名获取方法：resolve.activityInfo.packageName
        icon获取获取方法：resolve.loadIcon(packageManager)
        应用名称获取方法：resolve.loadLabel(packageManager).toString()
 */
        PackageManager pm = getPackageManager();


        for (ResolveInfo r : mApps) {
            Drawable ic = r.loadIcon((pm));
            if (ic == null) ic = defaultIcon;

            Item item = new Item(
                    r.loadLabel(pm).toString(),
                    r.activityInfo.packageName,
                    r.activityInfo.name,
                    ic
            );
            list.add(item);
        }

    }

    // 唯一在用的新方法
    private void loadAllApps(Intent intent) {
        List<ResolveInfo> mApps;
        mApps = new ArrayList<>();
        try {
            mApps.addAll(this.getPackageManager().queryIntentActivities(intent, 0));

            PackageManager pm = getPackageManager();
            for (ResolveInfo r : mApps) {
                Drawable ic = r.loadIcon((pm));
                if (ic == null) ic = defaultIcon;

                Item item = new Item(
                        r.loadLabel(pm).toString(),
                        r.activityInfo.packageName,
                        r.activityInfo.name,
                        ic
                );
                listOrgin.add(item);
            }
            Log.w("load", "" + listOrgin.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Intent makeIntent() {

        switch (_mode) {
            case "r2":
            case "r1":
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                return mainIntent;
            case "uri": {
                Uri uri = Uri.parse(_uri);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                return it;
            }

            case "uri_dail": {
                Uri uri = Uri.parse(_uri);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                return it;
            }

            case "uri_file": {
                File f = (new File(_uri));
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(Uri.fromFile(f), "*/*");
                return intent;
            }

            case "2nd": {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                //     intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory("android.intent.category.HOME");
                return intent;

            }
            case "short_cut": {
                Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);

                return shortcutsIntent;
            }

        }
        return null;

    }



}
