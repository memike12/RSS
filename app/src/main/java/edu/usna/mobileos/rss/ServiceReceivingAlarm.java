package edu.usna.mobileos.rss;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * This is my alarm receiver which calls an update to the feed when the alarm fires.
 */
public class ServiceReceivingAlarm extends IntentService {
    String  siteURL = "http://usnatrident.blogspot.com/feeds/posts/default?alt=rss";
    public ServiceReceivingAlarm() {
        super("ServiceRecievingAlarm");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Alarm", "Service started by alarm!!");
        Intent inte = new Intent(getBaseContext(), AsyncIntentService.class);
        inte.putExtra("URL", siteURL);
        startService(inte);
    }

}
