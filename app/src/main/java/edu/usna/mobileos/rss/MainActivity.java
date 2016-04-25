package edu.usna.mobileos.rss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public ListView newsListView;
    public ArrayAdapter<RssItem> adapter;
    public List<RssItem> rssItemList = new ArrayList<RssItem>();
    IntentFilter intentFilter;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            rssItemList = intent.getParcelableArrayListExtra("rss");
            Log.i("Here","gotasdf");
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

        String siteURL = "http://usnatrident.blogspot.com/feeds/posts/default?alt=rss";

        Intent intent = new Intent(getBaseContext(),AsyncIntentService.class);
        intent.putExtra("URL",siteURL);
        startService(intent);


    }

    @Override
    public void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction("WORK_COMPLETE_ACTION");
        Log.i("Here","got");
        registerReceiver(intentReceiver, intentFilter);
        adapter = new RssItemAdapter(this, android.R.layout.simple_list_item_1, rssItemList);
        newsListView.setAdapter(adapter);
        newsListView.setOnItemClickListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();

        // unregister the broadcast receiver
        unregisterReceiver(intentReceiver);
    }


    //------- AsyncTask ------------//
    class RetrieveFeedTask extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... urls) {
            try {
                URL feedURL = new URL(urls[0]);
                //rssItemList = parseRSS(feedURL);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 0;
        }

        protected void onPostExecute(Integer result) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        // actions to execute to do when an item is clicked

        RssItem RssItemClicked = adapter.getItem(pos);
        //Toast.makeText(getBaseContext(), RssItemClicked.getLink(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
        intent.putExtra("article", RssItemClicked.getLink());
        startActivity(intent);
    }
}
