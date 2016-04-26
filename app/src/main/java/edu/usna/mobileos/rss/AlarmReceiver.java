package edu.usna.mobileos.rss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This is my most pathetic class.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.i("Alarm", "Alarm triggered, starting service!!");

        Intent serviceIntent = new Intent(context,
                ServiceReceivingAlarm.class);
        context.startService(serviceIntent);
    }
}
