package com.example.danie.newsapp;

public class News {

    public final String title;
    public final String section;
    public final String authorName;
    public final String newsDate;
    public final String newsURL;

    public News(String title, String section, String authorName, String newsDate, String newsURL) {
        this.title = title;
        this.section = section;
        this.authorName = authorName;
        this.newsDate = newsDate;
        this.newsURL = newsURL;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public String getNewsURL() {
        return newsURL;
    }
}
