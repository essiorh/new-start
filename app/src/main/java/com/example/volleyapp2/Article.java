package com.example.volleyapp2;

import java.util.Date;

public class Article {
    private String url;
    private String title;
    private Date dat;

    public Article(String url, String title,Date dat) {
        this.url = url;
        this.title = title;
        this.dat=dat;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
    public Date getDat(){
        return dat;
    }
}
