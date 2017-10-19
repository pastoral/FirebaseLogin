package com.munir.harbingerstudio.firebasepoweredlogin.model;

/**
 * Created by munirul.hoque on 10/15/2017.
 */

public class News {
    private String title;
    private String author;
    private String imageUrl;

    public News() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
