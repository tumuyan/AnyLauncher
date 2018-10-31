
package com.tumuyan.fixedplay.App;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
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
//    SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(getContext().getDatabasePath("app"),null);

    public ItemAdapter(Context context, int layoutId, List<Item> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Item item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        final String packageName=item.getPackageName();

        ((ImageView) view.findViewById(R.id.item_img)).setImageDrawable(item.getAppIcon());
        ((TextView) view.findViewById(R.id.item_text)).setText(item.getName());
        ((TextView)view.findViewById(R.id.item_packageName)).setText(packageName);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm =getContext(). getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(packageName);
                if (intent != null) {

                    SharedPreferences.Editor editor = getContext().getSharedPreferences("setting", MODE_MULTI_PROCESS).edit();
                    editor.putString("app", packageName);
                    editor.commit();
                 //   getContext().startActivity(intent);
                    intent=new Intent(getContext() ,SettingActivity.class);
                    getContext().startActivity(intent);

                }else{
                    Toast.makeText(getContext(),"此应用无法设置为Luncher",Toast.LENGTH_SHORT).show();
                }
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


}


