package hankin.shortcuts;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private final String mDynamicShortcutId1 = "shortcuts_d1";
    private final String mPinShortcutId = "hankinhui_shortcut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent!=null && !TextUtils.isEmpty(intent.getAction())) {
            Log.d("mydebug---", "onCreate action : "+intent.getAction());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent!=null && !TextUtils.isEmpty(intent.getAction())) {
            Log.d("mydebug---", "onNewIntent action : "+intent.getAction());
        }
    }

    /*设置dynamic shortcut*/
    private void setDynamicShortcuts(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> infos = new ArrayList<>();

            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            ShortcutInfo info = new ShortcutInfo.Builder(context, mDynamicShortcutId1)
                    .setShortLabel(getString(R.string.shortcut_label_short1))
                    .setLongLabel(getString(R.string.shortcut_label_long1))
                    .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                    .setIntent(intent)
                    .build();
            infos.add(info);

            shortcutManager.setDynamicShortcuts(infos);
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        }
    }

    /*修改dynamic shortcut*/
    private void updateDynamicShortcuts(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> list = shortcutManager.getDynamicShortcuts();
            boolean has = false;
            for (int i=0;i<list.size();i++){
                if (mDynamicShortcutId1.equals(list.get(i).getId())){
                    has = true;
                    break;
                }
            }
            if (!has) return;//shortcuts中没有mDynamicShortcutId1，就返回

            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction("update");
            ShortcutInfo info = new ShortcutInfo.Builder(context, mDynamicShortcutId1)
                    .setShortLabel(getString(R.string.shortcut_label_short1)+"_up")
                    .setLongLabel(getString(R.string.shortcut_label_long1)+"_up")
                    .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                    .setIntent(intent)
                    .build();
            shortcutManager.updateShortcuts(Arrays.asList(info));
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        }
    }

    //移除dynamic shortcut
    private void removeDynamicShortcuts(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> list = shortcutManager.getDynamicShortcuts();
            for (int i=0;i<list.size();i++){
                ShortcutInfo info = list.get(i);
                if (mDynamicShortcutId1.equals(info.getId())){
                    shortcutManager.removeDynamicShortcuts(Arrays.asList(info.getId()));
                    return;
                }
            }
            Toast.makeText(this, "移除成功", Toast.LENGTH_SHORT).show();
        }
    }
    
    /*android8.0之前，生成快捷方式*/
    private void createShortCut(Context context){
        //创建快捷方式的Intent
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context.getApplicationContext(), R.mipmap.ic_launcher);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        //这种方式可以进入应用的stack队列顶部的activity，在桌面长按时，会出现卸载、删除两个选项
        Intent inte = new Intent(Intent.ACTION_MAIN);
        inte.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName comp = new ComponentName("hankin.shortcuts", "hankin.shortcuts.MainActivity");
        inte.setComponent(comp);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, inte);
        
        //这种方式只能进入指定的activity，在桌面长按时，只会出现删除一个选项
//        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(context.getApplicationContext() , MainActivity.class));

        context.sendBroadcast(intent);
    }

    /*添加应用桌面快捷方式*/
    public void addShortCut(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//api26
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            List<ShortcutInfo> infos = shortcutManager.getPinnedShortcuts();
            for (int i=0;i<infos.size();i++) {
                ShortcutInfo info = infos.get(i);
                if (info.getId().equals(mPinShortcutId)) {
                    if (info.isEnabled()) {//当pinned shortcut为disable时，重复创建会导致应用奔溃
                        break;
                    } else {
                        return;
                    }
                }
            }

            if (shortcutManager.isRequestPinShortcutSupported()) {

//                Intent inte = new Intent(context, ShortcutsActivity.class);
////                inte.setAction(Intent.ACTION_VIEW);

                Intent inte = new Intent(Intent.ACTION_MAIN);
                inte.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName comp = new ComponentName("hankin.shortcuts", "hankin.shortcuts.MainActivity");
                inte.setComponent(comp);

                ShortcutInfo info = new ShortcutInfo.Builder(context, mPinShortcutId)
                        .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                        .setShortLabel("shortcut_o")
                        .setIntent(inte)
                        .build();

                Intent reIn = new Intent(context, ShortcutsReciever.class);
                reIn.setAction(ShortcutsReciever.ACTION);
                PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, reIn, PendingIntent.FLAG_UPDATE_CURRENT);

                shortcutManager.requestPinShortcut(info, shortcutCallbackIntent.getIntentSender());
            }
        } else {
            createShortCut(this);
        }
        Toast.makeText(this, "创建快捷方式成功", Toast.LENGTH_SHORT).show();
    }

    /*使pinned shortcut无效*/
    private void disableShortcuts(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> infos = shortcutManager.getPinnedShortcuts();
            for (int i=0;i<infos.size();i++) {
                ShortcutInfo info = infos.get(i);
                if (info.getId().equals(mPinShortcutId)) {
                    shortcutManager.disableShortcuts(Arrays.asList(info.getId()), mPinShortcutId+"快捷方式没有效了");
                    break;
                }
            }
            Toast.makeText(this, "使无效成功", Toast.LENGTH_SHORT).show();
        }
    }

    public void click(View view){
        switch (view.getId()){
            case R.id.btn_main_set:
                setDynamicShortcuts(this);
                break;
            case R.id.btn_main_update:
                updateDynamicShortcuts(this);
                break;
            case R.id.btn_main_remove:
                removeDynamicShortcuts(this);
                break;
            case R.id.btn_main_create:
                addShortCut(this);
                break;
            case R.id.btn_main_disable:
                disableShortcuts(this);
                break;
        }
    }

}
