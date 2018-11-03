package com.tumuyan.fixedplay.App;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class SelectOne  extends Activity {

    private List<Item> list = new ArrayList<>();

    private ListView listView;
    private ProgressBar progressBar;
    private String _mode,_action,_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist_activity);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        Intent intent=getIntent();
         _mode=intent.getStringExtra("_mode");
         _action=intent.getStringExtra("_action");
         _uri=intent.getStringExtra("_uri");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //      getAppList();
              loadAllApps( makeIntent());
                final ItemAdapter itemAdapter = new ItemAdapter(SelectOne.this, R.layout.applist_item, list);
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
                        }

                });

            }

        }).start();
    }
    @Override
    protected void onPause(){
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
        for(ResolveInfo r:mApps){
            Item item = new Item(
                    r.loadLabel(pm).toString(),
                    r.activityInfo.packageName,
                    r.loadIcon(pm)
            );
            list.add(item);
        }

    }


    private void loadAllApps(Intent intent) {
        List<ResolveInfo> mApps;
        mApps = new ArrayList<>();
        try {
            mApps.addAll(this.getPackageManager().queryIntentActivities(intent, 0));

            PackageManager pm = getPackageManager();
            for (ResolveInfo r : mApps) {
                Item item = new Item(
                        r.loadLabel(pm).toString(),
                        r.activityInfo.packageName,
                        r.activityInfo.name,
                        r.loadIcon(pm)
                );
                list.add(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Intent makeIntent(){

        switch (_mode){
            case "r2":
            case "r1":
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                return mainIntent;
            case "uri":
            {
                Uri uri = Uri.parse(_uri);
                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                return it;
            }

            case "uri_dail":
            {
                Uri uri = Uri.parse(_uri);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                return it;
            }

            case "uri_file":
            {  File f=(new File(_uri));
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(Uri.fromFile(f), "*/*");
                return intent;
            }




        }
return null;

    }



}
