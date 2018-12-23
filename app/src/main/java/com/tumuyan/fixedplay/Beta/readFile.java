package com.tumuyan.fixedplay.Beta;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.tumuyan.fixedplay.App.Item;


import net.dongliu.apk.parser.ApkFile;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class readFile {

    public static void parserAxml(String filePath){
        try {
            ApkFile apkFile = new ApkFile(new File(filePath));
            String manifestXml = apkFile.getManifestXml();
            String xml = apkFile.transBinaryXml("AndroidManifest.xml");
        //  Log.w("get xml",xml);
            InputStream is = new ByteArrayInputStream(xml.getBytes());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ArrayList<String> pullXml (String path,String packageName,String activityName) throws Exception {

        //  public ArrayList<Item> pull2xml(String packageName, Context context) throws Exception {
        //  String path=context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        boolean findActivity=false;
        String result="";
        IntentFilter filter=new IntentFilter();
        ArrayList<IntentFilter> filters=new ArrayList<>();

        ApkFile apkFile = new ApkFile(new File(path));
        String manifestXml = apkFile.getManifestXml();
        String xml = apkFile.transBinaryXml("AndroidManifest.xml");
        //  Log.w("get xml",xml);
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();

        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        while (type != XmlPullParser.END_DOCUMENT) {
           // Log.i("get  ",parser.getAttributeCount());
            if(type==XmlPullParser.START_TAG && !findActivity){

                if (("activity".equals(parser.getName()))) {
/*                    for(int i=parser.getAttributeCount()-1;i>-1;i--){
                        Log.i("get activity att"+i,parser.getAttributeName(i)+"-"+parser.getAttributeValue(i));
                    }*/
                    String className = parser.getAttributeValue(null ,"name" );
                    Log.i("get activity name",parser.getAttributeValue(null, "name")+"-aim:"+activityName);
                 //   Log.i("get activity a name",parser.getAttributeValue(null, "android:name")+"-aim:"+activityName);
                    if (activityName.equals(className)) {
                        findActivity = true;
                    } else if (activityName.equals(packageName + className)) {
                        findActivity = true;
                    }else{
                        Log.i("no mach","");
                    }
                }
            }else if(type==XmlPullParser.START_TAG && findActivity){

                switch ((parser.getName())){

                    case "intent-filter":
                        filter.clear();
                        break;
                    case "action":
                        filter.addAction(parser.getAttributeValue(null,"name"));
                        break;
                    case "category":
                        filter.addCategory(parser.getAttributeValue(null,"name"));
                        break;

                }


            }else if(type==XmlPullParser.END_TAG) {

                if("intent-filter".equals(parser.getName()) && filter.hasContent()){
                    filters.add(filter);
                }

                else if ("activity".equals(parser.getName()) && findActivity) {
                    break;
                }

            }
            //继续往下读取标签类型
            type = parser.next();
        }

        for(int i=0;i<filters.size();i++){
            result=result+"\nIntent"+i+":\n"+filters.get(i).getString();

        } Log.w("read result",result);
        ArrayList<String> actions=new ArrayList<>();
        actions.add("none");
        actions.add("android.intent.action.MAIN");
        actions.add("android.intent.action.VIEW");
        for(IntentFilter intentFilter:filters){
            for(String action:intentFilter.getAction()){
                actions.remove(action);
                actions.add(action);
            }
        }
/*
        String[] actionStr=new String[actions.size()];
        for(int i=0;i<actions.size();i++){
            actionStr[i]=actions.get(i);
        }
*/

        return actions;
    }


    public static String[] getStrings(ArrayList<String> arrayList){
        String[] actionStr=new String[arrayList.size()];
        for(int i=0;i<arrayList.size();i++){
            actionStr[i]=arrayList.get(i);
        }
        return actionStr;
    }

    public static String getString(ArrayList<String> arrayList){
        String str="";
        for(String s:arrayList){
            str=str+"\n"+s;
        }
        return str;
    }

    public static String   pull2xml(String path,String packageName,String activityName) throws Exception {

      //  public ArrayList<Item> pull2xml(String packageName, Context context) throws Exception {
          //  String path=context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        boolean findActivity=false;
        String result="";
        IntentFilter filter=new IntentFilter();
        ArrayList<IntentFilter> filters=new ArrayList<>();

        InputStream is=readAppFile(path);
            //创建xmlPull解析器
            XmlPullParser parser = Xml.newPullParser();

            ///初始化xmlPull解析器
            parser.setInput(is, "utf-8");
            //读取文件的类型
            int type = parser.getEventType();
            //无限判断文件类型进行读取
            while (type != XmlPullParser.END_DOCUMENT) {
                if(type==XmlPullParser.START_TAG && !findActivity){

                   if ((parser.getName().equals("activity"))) {
                            String className = parser.getAttributeValue("", "android:name");
                            if (className.equals(activityName)) {
                                findActivity = true;
                            } else if (activityName.equals(packageName + className)) {
                                findActivity = true;
                            }
                    }
                }else if(type==XmlPullParser.START_TAG && findActivity){

                    switch ((parser.getName())){

                        case "intent-filter":
                            filter.clear();
                            break;
                        case "action":
                            filter.addAction(parser.getAttributeValue("","android:name"));
                            break;
                        case "category":
                            filter.addCategory(parser.getAttributeValue("","android:name"));
                            break;

                    }


                }else if(type==XmlPullParser.END_TAG) {

                    if("intent-filter".equals(parser.getName()) && filter.hasContent()){
                        filters.add(filter);
                    }

                    else if ("activity".equals(parser.getName()) && findActivity) {
                        break;
                    }

                }
                //继续往下读取标签类型
                type = parser.next();
            }

            for(int i=0;i<filters.size();i++){
                result=result+"\nIntent"+i+":\n"+filters.get(i).getString();

            } Log.w("read result",result);
            return result;
        }



    public static String  getXmlData(String path){
        try {
            //得到XML解析器
            XmlPullParser pullParser = Xml.newPullParser();
          //  InputStream is = getAssets().open("activity_main.xml");
            InputStream is=readAppFile(path);
            pullParser.setInput(is, "utf-8");
            //得到事件类型
            int eventType = pullParser.getEventType();
            //文档的末尾
            //遍历内部的内容
            StringBuilder stringBuilder = new StringBuilder();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = pullParser.getName();
                if (!TextUtils.isEmpty(name))
                    if (eventType == XmlPullParser.START_TAG) {
                        String attributeValue = pullParser.getAttributeValue(null, "id");
                        attributeValue = attributeValue.substring(attributeValue.indexOf("/") + 1, attributeValue.length());

                        stringBuilder.append("name====");
                        stringBuilder.append(name);
                        stringBuilder.append("\t\tid====");
                        stringBuilder.append(attributeValue);
                        stringBuilder.append("\n\n");
                    }
                eventType = pullParser.next();//读取下一个标签
            }
             return  (stringBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
return "";
    }


    public static InputStream readAppFile(String file) throws IOException {
        String fileName =file.replaceFirst("^.*/","").replaceFirst("\\.[a-zA-Z0-9]+$","");
        ZipFile zf = new ZipFile(file);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            Log.e("get steam","x "+ze.getName());
            if (ze.isDirectory()) {
                //Do nothing
            } else {
                if (ze.getName().equals("AndroidManifest.xml")) {
                    InputStream inputStream = zf.getInputStream(ze);
                    Log.e("get steam","mathced xml");
                    return inputStream;
                }
            }
        }
        zin.closeEntry();
        return null;
    }


    public static String readDataFile(String file) throws Exception {
        //截取路径的文件名 res
        String fileName =file.replaceFirst("^.*/","").replaceFirst("\\.[a-zA-Z0-9]+$","");
        ZipFile zf = new ZipFile(file);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.isDirectory()) {
                //Do nothing
            } else {
                if (ze.getName().equals(fileName + "/AndroidManifest.xml")) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(zf.getInputStream(ze)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        return line;
                    }
                    br.close();
                    break;
                }
            }
        }
        zin.closeEntry();
        return "";
    }
}
