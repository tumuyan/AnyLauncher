package com.tumuyan.fixedplay.App;

import android.graphics.drawable.Drawable;

public class Item {
    private String name;
    private int imgId;
    private Drawable appIcon=null;
    private String packageName;
    private boolean isChecked=false;

    public Item(String name, int id) {
        this.name = name;
        this.imgId = id;
    }

    public Item(String name,String packageName, Drawable appIcon){
        this.name=name;
        this.packageName=packageName;
        this.appIcon=appIcon;

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

    public int getImgId() {
        return imgId;
    }
}