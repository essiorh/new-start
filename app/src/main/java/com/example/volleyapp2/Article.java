package com.example.volleyapp2;

import java.util.Date;

public class Article {
    private String url;
    private String title;
    private String dat;

    public Article(String url, String title,String dat) {
        this.url = url;
        this.title = title;
        this.dat=dat;
    }
    public Article() {
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
    public String getDat(){
        return dat;
    }
}
