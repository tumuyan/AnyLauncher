package com.tumuyan.fixedplay.App;

import android.graphics.drawable.Drawable;

public class Item {
    private String name;
    private String packageName;
    private String className="";
    private Drawable appIcon;


    public Item(String name,String packageName, Drawable appIcon){
        this.name=name;
        this.packageName=packageName;
        this.appIcon=appIcon;
    }

    public Item(String name,String packageName, String className,Drawable appIcon){
        this.name=name;
        this.packageName=packageName;
        this.className=className;
        this.appIcon=appIcon;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

}