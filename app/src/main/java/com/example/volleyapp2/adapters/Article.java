package com.example.volleyapp2.adapters;

public class Article {
    private String url;
    private String title;
    private String dat;
    private String uri;

    public Article(String url, String title, String dat,String uri) {
        this.url = url;
        this.title = title;
        this.dat = dat;
        this.uri = uri;
    }

    public Article() {
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDat() {
        return dat;
    }

    public String getUri() {
        return uri;
    }
}
