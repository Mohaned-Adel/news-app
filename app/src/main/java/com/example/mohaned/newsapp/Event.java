package com.example.mohaned.newsapp;

import android.widget.ImageView;

/**
 * Created by Mohaned on 5/11/2018.
 */

public class Event {

    /** Image url src */
    private String imageUrl;

    /** User name of the article */
    private String articleName;

    /** Date that the article happened */
    private String date;

    /** the title of the article */
    private String webTitle;

    /** the type of the article */
    private String type;

    /** the section name of the article */
    private String section;

    /** the url source */
    private String url;

    public Event (String eventImageUrl, String eventName, String eventTitle, String eventDate, String eventType, String eventSection, String eventUrl) {

        imageUrl = eventImageUrl;
        articleName = eventName;
        webTitle = eventTitle;
        date = eventDate;
        type = eventType;
        section = eventSection;
        url = eventUrl;
    }

    public String getImageUrl() { return imageUrl; }
    public String getArticleName() { return articleName; }
    public String getWebTitle() { return webTitle; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getSection() { return section; }
    public String getUrl() { return url; }
}
