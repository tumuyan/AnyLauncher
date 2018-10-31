package com.tumuyan.fixedplay.App;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tumuyan.fixedplay.MainActivity;
import com.tumuyan.fixedplay.R;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class SelectOne  extends Activity {

    private List<Item> list = new ArrayList<>();
    private int result_position;
    private Button sltChannel, sltOK;
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist_activity);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getAppList();
                final ItemAdapter itemAdapter = new ItemAdapter(SelectOne.this, R.layout.applist_item, list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //耗时操作，需要在子线程中完成操作后通知主线程实现UI更新

                        listView = (ListView) findViewById(R.id.listview);
                        if (listView == null) Log.e("listitem", "null");
                        listView.setAdapter(itemAdapter);
                        progressBar.setVisibility(View.GONE);
                        }

                });

            }

        }).start();
    }

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

}
