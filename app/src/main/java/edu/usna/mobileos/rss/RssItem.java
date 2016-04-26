package edu.usna.mobileos.rss;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * WHAT PARCELABLE!?!
 *
 * ... necessary.
 */
public class RssItem implements Parcelable{

    private String link;
    private String pubDate;
    private String description;
    private String title;

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getPubDate() {
        return pubDate;
    }
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public static final Parcelable.Creator<RssItem> CREATOR
            = new Parcelable.Creator<RssItem>() {
        public RssItem createFromParcel(Parcel in) {
            return new RssItem(in);
        }

        public RssItem[] newArray(int size) {
            return new RssItem[size];
        }
    };

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(pubDate);
        dest.writeString(description);
        dest.writeString(title);
    }

    private RssItem(Parcel in) {
        link = in.readString();
        pubDate = in.readString();
        description = in.readString();
        title = in.readString();

    }

    public RssItem() {
    }
}