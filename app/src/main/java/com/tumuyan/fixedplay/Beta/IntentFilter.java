package com.tumuyan.fixedplay.Beta;

import java.util.ArrayList;

public class IntentFilter {

    public class StringPair{
        String name,value;

        StringPair(String name,String value){
            this.name=name;
            this.value=value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    private ArrayList<String> action=new ArrayList<>();
    private ArrayList<String> category=new ArrayList<>();
    private ArrayList<StringPair> data=new ArrayList<>();
    //未完成

    IntentFilter(){

    }

    public void clear(){
        action.clear();
        category.clear();
        data.clear();
    }

    public void addAction(String Action){
       // action.remove(Action);
        action.add(Action);
    }
    public void addCategory(String Category){
     //   category.remove(Category);
        category.add(Category);
    }

    public ArrayList<String> getAction() {
        return action;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public ArrayList<StringPair> getData() {
        return data;
    }

    public ArrayList<StringPair> getData(String name) {

        return data;
    }

    public int getActionSize(){
        return action.size();
    }

    public int getCategorySize(){
        return category.size();
    }

    public boolean hasContent(){
        if(action.size()+category.size()>0){
            return true;
        }
        return false;
    }

    public String getString(){
        String result="";
        for(String s:action){
            result=result+"action:"+s+"\n";
        }
        for(String s:category){
            result=result+"category:"+s+"\n";
        }
        return result;
    }
}
