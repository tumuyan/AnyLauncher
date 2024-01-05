
package com.tumuyan.fixedplay.App;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
//import android.database.sqlite.SQLiteDatabase;
//import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tumuyan.fixedplay.R;
import com.tumuyan.fixedplay.SettingActivity;

import java.util.List;

import static android.content.Context.MODE_MULTI_PROCESS;


/**
 * Created by baniel on 1/19/17.
 */

public class ItemAdapter extends ArrayAdapter<Item> {
    private int layoutId;
    private String mode="r2";
    private String uri="";
//    SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(getContext().getDatabasePath("app"),null);

    public ItemAdapter(Context context, int layoutId, List<Item> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

  //  @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Item item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        final String packageName = item.getPackageName();
        final String className = item.getClassName();
        final String Name = item.getName();

        String _class, _package, _name;
        if (className.length() < 1) {
            _name = Name;
            _package = packageName;
        } else if (className.contains(packageName)) {
            _class = className.replace(packageName, "");
            _package = packageName;
            _name = Name + "(" + _class + ")";
        } else {
            _name = Name;
            _package = packageName + "\n" + className;
        }

        ((ImageView) view.findViewById(R.id.item_img)).setImageDrawable(item.getAppIcon());
        ((TextView) view.findViewById(R.id.item_text)).setText(_name);
        ((TextView)view.findViewById(R.id.item_packageName)).setText(_package);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(Name,packageName,className);
            }
        });


        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                int keycode = keyEvent.getKeyCode();
                if (keycode == KeyEvent.KEYCODE_ENTER || keycode == KeyEvent.KEYCODE_NUMPAD_ENTER || keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    Log.i("App Selector","Keycode match, action = "+keyEvent.getAction());
                    select(Name,packageName,className);
                    return true;
                }else{
                    Log.i("App Selector","Keycode = "+keycode);
                }
                return false;
            }
        });

        return view;
    }


    /**
     * @param
     * @描述 通过包名启动其他应用，假如应用已经启动了在后台运行，则会将应用切到前台
     * @作者 tll
     * @时间 2017/2/7 17:40
     */
    public static void startActivityForPackage(Context context, String packName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packName);
        context.startActivity(intent);
    }

    public void select(String Name, String packageName, String className){

        if(mode.equals("2nd")){
            SharedPreferences.Editor editor = getContext(). getSharedPreferences("setting",MODE_MULTI_PROCESS).edit();
            editor.putString("app_2nd", packageName);
            editor.putString("label_2nd", Name);
            editor.putString("class_2nd",className);
            editor.commit();
            Intent intent=new Intent(getContext() ,SettingActivity.class);
            getContext().startActivity(intent);
        }else{
            PackageManager pm =getContext(). getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(packageName);

            if (intent != null) {

                {
                    SharedPreferences.Editor editor = getContext(). getSharedPreferences("setting",MODE_MULTI_PROCESS).edit();
                    editor.putString("app", packageName);
                    editor.putString("label", Name);
                    editor.putString("class",className);
                    editor.putString("uri",uri);
                    editor.putString("mode",mode);
                    editor.commit();
                }

                //   getContext().startActivity(intent);
                intent=new Intent(getContext() ,SettingActivity.class);
                getContext().startActivity(intent);

            }else{
                Toast.makeText(getContext(),R.string.error_could_not_start,Toast.LENGTH_SHORT).show();
            }

        }


    }

}


