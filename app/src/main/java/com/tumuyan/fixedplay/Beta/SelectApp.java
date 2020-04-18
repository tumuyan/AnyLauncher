package com.tumuyan.fixedplay.Beta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tumuyan.fixedplay.App.Item;
import com.tumuyan.fixedplay.App.ItemAdapter;
import com.tumuyan.fixedplay.App.SelectOne;
import com.tumuyan.fixedplay.R;
import com.tumuyan.fixedplay.SettingActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SelectApp extends Activity {
    // Adapter显示项目（筛选后
    private List<Item> list = new ArrayList<>();
    // 原始搜索结果
    private List<Item>  listOrgin=new ArrayList<>();


    private ListView listView,listviewActivity;
    private ProgressBar progressBar;
    private String _mode,_action,_uri;

    private View SearchBox;
    private EditText SearchText;
    private Button SearchButton;
    private   mixAdapter itemAdapter;
    private Drawable defaultIcon;
    private Boolean showActivity=false;
    private PackageManager pm;
    private String packageName="", label="",action,className;
    private TextView TextBottom ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbeta_activity);
        TextBottom=(TextView)findViewById(R.id.textBottom);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        SearchBox=(View)findViewById(R.id.searchBox);
        SearchButton=(Button)findViewById(R.id.button_filter);
        SearchText=(EditText) findViewById(R.id.editText_filter);
        SearchBox.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.listview);
        listviewActivity=(ListView)findViewById(R.id.listview_acitivity);
        listviewActivity.setVisibility(View.GONE);

        defaultIcon=getResources().getDrawable(R.drawable.unknow);

        itemAdapter = new mixAdapter(SelectApp.this, R.layout.applist_item, list);
        itemAdapter.setMode(_mode);
        itemAdapter.setUri(_uri);
/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.w("selectapp","1"+",i"+i+",L"+l);
                // Toast.makeText(SelectApp.this, "Click item" + i, Toast.LENGTH_SHORT).show();
            }
        });*/
        //ListView item 中的删除按钮的点击事件
        itemAdapter.setOnItemDeleteClickListener(new mixAdapter.onItemDeleteListener() {
            @Override
            public void onDeleteClick(int i) {


                if(showActivity){
                  //  TextBottom.setText(list.get(i).getExt());
                    try{
                        String path=getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
                        className=list.get(i).getClassName();
                        if(className.matches("^\\..*")){
                            className=packageName+className;
                        }
                        label=list.get(i).getName();
                        ArrayList<String> actions=  readFile.pullXml(path,packageName,className);
                        if(actions.size()>0){
                            final String[] actionStr=new String[actions.size()];
                            for(int j=0;j<actions.size();j++){
                                actionStr[j]=actions.get(j);
                            }
                            AlertDialog alertDialog3 = new AlertDialog.Builder(SelectApp.this)
                                    .setTitle("选择工作模式")
                                    //   .setIcon(R.mipmap.ic_launcher)
                                    .setItems(actionStr, new DialogInterface.OnClickListener() {//添加列表
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            action=actionStr[i];
                                            saveAndExit();

                                        }
                                    })
                                    .create();
                            alertDialog3.show();

                        }

/*
                        *//*      Log.w("apk path",path);*//*
                      TextBottom.setText(
                        //        readFile.getXmlData(path)

                                readFile.getString(  readFile.pullXml(path,packageName,list.get(i).getClassName()))
                        );*/
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    Log.w("selectapp","2-"+i);
                    packageName=list.get(i).getPackageName();
//                list.remove(i);
//               itemAdapter.notifyDataSetChanged();
                    showActivity=true;
                    selectActivity();
                }


            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAppList();
                //       loadAllApps( makeIntent());
              list.addAll(listOrgin);
              itemAdapter.notifyDataSetChanged();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //耗时操作，需要在子线程中完成操作后通知主线程实现UI更新
                        if (listView == null) Log.e("listitem", "null");
                        listView.setAdapter(itemAdapter);
                        progressBar.setVisibility(View.GONE);
                        SearchBox.setVisibility(View.VISIBLE);
                        }
                });
            }

        }).start();

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("search","-");
                doSearch(SearchText.getText().toString());
            }
        });
    }

    private void saveAndExit(){
        SharedPreferences.Editor editor =  getSharedPreferences("setting",MODE_MULTI_PROCESS).edit();
        editor.putString("app", packageName);
        editor.putString("label", label);
        editor.putString("class",className);
        editor.putString("uri","");
        editor.putString("action",action);
        editor.putString("mode","beta");
        editor.commit();
        Log.w("save action",action);
        Intent intent=new Intent(SelectApp.this,SettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;

        startActivity(intent);
    }

    private void selectActivity(){
        Log.w("select activity","0");
       // itemAdapter.notifyDataSetChanged();
        if(showActivity){
//            listviewActivity.setVisibility(View.VISIBLE);
//            listView.setVisibility(View.GONE);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try{

                        //flag值必须包含PackageManager.GET_ACTIVITIES，
                        // 如果还要获取其他信息如metadata则增加一个flag值
                        // PackageManager.GET_META_DATA
                        PackageInfo  packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

/*                    //flag值必须包含PackageManager.GET_RECEIVERS，如果还要获取其他信息如metadata则增加一个flag值PackageManager.GET_META_DATA
                    PackageInfo packageInfo = pm.getPackageInfo(packageName,PackageManager.GET_RECEIVERS);
                    //broadcastreceiver信息
                    ActivityInfo[] receivers = packageInfo.receivers;*/

                        //activity信息
                        ActivityInfo[] activities = packageInfo.activities;

                        list.clear();

                        for(ActivityInfo activityinfo:activities){
                            Item item=new Item(
                                    activityinfo.loadLabel(pm).toString(),
                                    packageName,
                                    activityinfo.name,
                                    activityinfo.loadIcon(pm),
                                    activityinfo

                            );
                            list.add(item);
                        }



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                SearchBox.setVisibility(View.VISIBLE);
                                itemAdapter.notifyDataSetChanged();
                            }
                        });



                    }catch (Exception e){e.printStackTrace();}

                }

            }).start();
        }

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

            private void doSearch (String  str){
                if(showActivity){
                    ArrayList<Item> scoredA=new ArrayList<>();
                    ArrayList<Item> scoredB=new ArrayList<>();
                    for(int i=0;i<list.size();i++){
                        Item item=list.get(i);
                        if(item.getClassName().contains(str) ||
                                item.getPackageName().contains(str)||
                                item.getName().contains(str)){
                           scoredA.add(item);
                        }else{
                            scoredB.add(item);
                        }
                    }
                    list.clear();
                    list.addAll(scoredA);
                    list.addAll(scoredB);

                }else{
                    list.clear();
                    for(int i=0;i<listOrgin.size();i++){
                        Item item=listOrgin.get(i);
                        if(item.getClassName().contains(str) ||
                                item.getPackageName().contains(str)||
                                item.getName().contains(str)){
                            list.add(item);
                        }
                    }
                    Log.w("search", str+" get"+list.size()+" from "+listOrgin.size());
                }
                itemAdapter.notifyDataSetChanged();
            }



    @Override
    protected void onPause(){
        super.onPause();
        this.finish();
    }
    // 停用的旧方法
    private void getAppList() {
        pm = getPackageManager();
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
*/           Drawable ic=  pm.getApplicationIcon(packageInfo.applicationInfo);
            if(null==ic) ic=defaultIcon;

            Item item = new Item(
                    pm.getApplicationLabel(packageInfo.applicationInfo).toString(),
                    packageInfo.applicationInfo.packageName,
                    ic
            );

            listOrgin.add(item);

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
            Drawable ic=r.loadIcon((pm));
            if(ic==null) ic=defaultIcon;

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
                Drawable ic=r.loadIcon((pm));
                if(ic==null) ic=defaultIcon;

                Item item = new Item(
                        r.loadLabel(pm).toString(),
                        r.activityInfo.packageName,
                        r.activityInfo.name,
                        ic
                );
                listOrgin.add(item);
            }
            Log.w("load",""+listOrgin.size());
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
