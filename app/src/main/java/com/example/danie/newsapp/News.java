package com.example.danie.newsapp;

public class News {

    public final String title;
    private final String section;
    private final String authorName;
    private final String newsDate;
    private final String newsURL;

    News(String title, String section, String authorName, String newsDate, String newsURL) {
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
