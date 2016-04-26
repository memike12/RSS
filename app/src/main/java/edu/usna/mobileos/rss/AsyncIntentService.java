package edu.usna.mobileos.rss;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * AsyncIntentService takes a URL and then sends back the rssItemList it creates
 */
public class AsyncIntentService extends IntentService{
    public ArrayList<RssItem> rssItemList = new ArrayList<RssItem>();

    public AsyncIntentService() {
        super("AsyncIntentServiceName");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Feed", "AsyncIntentService started working");
        String url = intent.getStringExtra("URL");
        DoMuchWork(url);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("WORK_COMPLETE_ACTION");

        broadcastIntent.putParcelableArrayListExtra("rss", rssItemList);
        getBaseContext().sendBroadcast(broadcastIntent);
    }

    private void DoMuchWork( String url) {
        try {
            URL feedURL = new URL(url);
            rssItemList = parseRSS(feedURL);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<RssItem> parseRSS(URL feedURL)
            throws XmlPullParserException, IOException {

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(feedURL.openStream(), null);

        int eventType = parser.getEventType();

        boolean done = false;

        RssItem currentRSSItem= new RssItem();

        while (eventType != XmlPullParser.END_DOCUMENT && !done) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item")) {
                        // a new item element
                        currentRSSItem = new RssItem();
                    } else if (currentRSSItem != null) {
                        if (name.equalsIgnoreCase("link")) {
                            currentRSSItem.setLink(parser.nextText());
                        } else if (name.equalsIgnoreCase("description")) {
                            currentRSSItem.setDescription(parser.nextText());
                        } else if (name.equalsIgnoreCase("pubDate")) {
                            currentRSSItem.setPubDate(parser.nextText());
                        } else if (name.equalsIgnoreCase("title")) {
                            currentRSSItem.setTitle(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && currentRSSItem != null) {
                        rssItemList.add(currentRSSItem);
                    } else if (name.equalsIgnoreCase("channel")) {
                        done = true;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return rssItemList;
    }
}
