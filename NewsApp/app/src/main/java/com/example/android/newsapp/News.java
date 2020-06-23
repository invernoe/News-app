package com.example.android.newsapp;

class News {
    private String mTitle;
    private String mUrl;
    private String mSection;
    private String mPublicationDate;
    private String mAuthor;

    News(String title,String url,String section,String date,String author){
        mTitle = title;
        mUrl = url;
        mSection = section;
        mPublicationDate = date;
        mAuthor = author;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getSection() {
        return mSection;
    }

    public String getPublicationDate() {
        return mPublicationDate;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
