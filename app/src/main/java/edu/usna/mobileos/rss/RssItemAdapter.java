package edu.usna.mobileos.rss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * This is a sweet adapter that displays a title and a date that's formatted so poorly,
 * it hurts the eyes
 */
public class RssItemAdapter extends ArrayAdapter<RssItem> {

    private Context context;

    public RssItemAdapter(Context context, int textViewResourceId,
                          List<RssItem> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_layout, null);
        }

        RssItem item = getItem(position);
        if (item != null) {
            // our layout has two TextView elements
            TextView titleView = (TextView) view.findViewById(R.id.titleText);
            TextView descView = (TextView) view
                    .findViewById(R.id.dateText);

            titleView.setText(item.getTitle());
            descView.setText(item.getPubDate());
        }

        return view;
    }
}
