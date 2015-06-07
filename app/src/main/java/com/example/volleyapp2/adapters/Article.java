package com.example.volleyapp2.adapters;

/**
 * Class for article in our list
 * @author ilia
 */
public class Article {
    private String url;
    private String title;
    private String dat;
    private String uri;

    /**
     * Constructor with the parameters
     * @param url Url in picture
     * @param title Title current art
     * @param dat Date publication current art
     * @param uri Uri current art for address
     */
    public Article(String url, String title, String dat,String uri) {
        this.url = url;
        this.title = title;
        this.dat = dat;
        this.uri = uri;
    }

    /**
     * default constructor
     */
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
