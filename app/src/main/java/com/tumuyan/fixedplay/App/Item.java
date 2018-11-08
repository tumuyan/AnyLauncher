package com.tumuyan.fixedplay.App;

import android.graphics.drawable.Drawable;

public class Item {
    private String name;
    private String packageName;
    private String className="";
    private Drawable appIcon;
    private String ext="";

/*
    private int position=0;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }*/
    public Item(){

    }
    public Item(String name, String packageName){
        this.name=name;
        this.packageName=packageName;

    }
    public Item(String name, String packageName, Drawable appIcon){
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

    public Item(String name,String packageName, String className,Drawable appIcon,Object ext){
        this.name=name;
        this.packageName=packageName;
        this.className=className;
        this.appIcon=appIcon;
        this.ext=""+ext;
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

    public void setExt(Object ext) {
        this.ext = ""+ext;
    }

    public String getExt() {
        return ext;
    }
}