package hankin.shortcuts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 * Created by Hankin on 2017/10/18.
 * @email hankin.huan@gmail.com
 */

public class ShortcutsReciever extends BroadcastReceiver {

    public static final String ACTION = "hankin.shortcuts.ShortcutsReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("mydebug---", "ShortcutsReciever onReceive : "+intent.getAction());
    }

}
