package com.example.mohaned.newsapp;

/**
 * Created by Mohaned on 5/11/2018.
 */

public class Event {

    /** User name of the article */
    public final String name;

    /** Date that the article happened */
    public final String date;

    /** the title of the article */
    public final String webTitle;

    public Event (String eventName, String eventDate, String eventTitle) {
        name = eventName;
        date = eventDate;
        webTitle = eventTitle;
    }
}
