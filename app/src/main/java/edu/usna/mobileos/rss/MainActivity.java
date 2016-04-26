package edu.usna.mobileos.rss;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/********************
 * Main Activity is the work horse of the app.
 * I completed this assignment with only my own blood, sweat, and tears.
 * WOW. 10/10 for effiency! Actually, I used asyncTask for really no reason to set the alarm.
 * I thought it might be fun.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public ListView newsListView;
    public ArrayAdapter<RssItem> adapter;
    public List<RssItem> rssItemList = new ArrayList<RssItem>();
    IntentFilter intentFilter;
    String siteURL;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;
    AlarmManager alarmManager;
    long alarmSeconds;
    Calendar calendar;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            rssItemList = intent.getParcelableArrayListExtra("rss");
            adapter = new RssItemAdapter(context, android.R.layout.simple_list_item_1, rssItemList);
            newsListView.setAdapter(adapter);
            Log.i("Feed","Loaded RSS feed");

            generateNotification(context, "Refreshed", "Feed has been refreshed and is ready to view.",
                    1775);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsListView = (ListView) findViewById(R.id.newsListView);
        adapter = new RssItemAdapter(this, android.R.layout.simple_list_item_1, rssItemList);
        newsListView.setAdapter(adapter);
        newsListView.setOnItemClickListener(this);
        alarmSeconds = 60;
        calendar = Calendar.getInstance();

        siteURL = "http://usnatrident.blogspot.com/feeds/posts/default?alt=rss";

        if(isConnectedToInternet(this)) {
            Intent intent = new Intent(getBaseContext(), AsyncIntentService.class);
            intent.putExtra("URL", siteURL);
            Log.i("Feed", "about to start work");
            startService(intent);
        }
        else{
            RssItem rssItem = new RssItem();
            rssItem.setTitle("Error connecting to the internet");
            rssItemList.add(rssItem);
            adapter = new RssItemAdapter(this, android.R.layout.simple_list_item_1, rssItemList);
            newsListView.setAdapter(adapter);
        }
        alarmManager = (AlarmManager) getBaseContext()
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);

        new SetAlarmTask().execute();
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction("WORK_COMPLETE_ACTION");
        Log.i("Feed","Work Complete");
        registerReceiver(intentReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // unregister the broadcast receiver
        unregisterReceiver(intentReceiver);
    }


    //------- AsyncTask ------------//
    class SetAlarmTask extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... urls) {
            setAlarm(findViewById(android.R.id.content));
            return 0;
        }

        protected void onPostExecute(Integer result) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        // actions to execute to do when an item is clicked
        if(isConnectedToInternet(this)) {

            RssItem RssItemClicked = adapter.getItem(pos);
            Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
            intent.putExtra("article", RssItemClicked.getLink());
            startActivity(intent);
        }
        else{
            RssItem rssItem = new RssItem();
            rssItem.setTitle("Error connecting to the internet");
            rssItemList.add(rssItem);
            adapter = new RssItemAdapter(this, android.R.layout.simple_list_item_1, rssItemList);
            newsListView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle item selection
        switch(item.getItemId()){
            case R.id.reload:
                Intent intent = new Intent(getBaseContext(),AsyncIntentService.class);
                intent.putExtra("URL",siteURL);
                Log.i("Feed", "about to start");
                startService(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        Log.e("ERROR", "NetworkUtil - Not Connected to Network");
        return false;
    }

    public static void generateNotification(Context context, String title,
                                            String message, int notificationId) {

        // get instance of notification manager
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);

        // intent to specify which activity to launch when the
        // notification is selected
        Intent notificationIntent = new Intent(context,
                MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // pending intent to allow the system to launch the activity
        // inside our app from the notification
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public static void cancelNotification(Context context, int notificationId) {
        try{
            NotificationManager notificationManager =
                    (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
        } catch (Exception e) {
            Log.e("Notification", "cancelNotification() error: " + e.getMessage());
        }
    }

    public void setAlarm(View view){
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60, pendingIntent);
        Log.i("Alarm", "Alarm set for once a minute");
    }

    public void cancelAlarm(View view){
        if (alarmManager!= null) {
            alarmManager.cancel(pendingIntent);
        }
        Log.i("Alarm", "Alarm cancelled");
        setAlarm(view);
    }
}
