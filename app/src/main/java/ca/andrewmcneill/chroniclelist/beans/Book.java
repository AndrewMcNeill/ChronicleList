package ca.andrewmcneill.chroniclelist.beans;

import androidx.annotation.NonNull;

public class Book {
    private String title;
    private String author;
    private double rating;
    private String coverUrl;
    private String description;
    private String apiID;

    public Book(String apiID, String title, String author, double rating, String coverUrl) {
        this.apiID = apiID;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.coverUrl = coverUrl;
        this.description = "";
    }

    public Book(String apiID, String title, String author, double rating, String coverUrl, String description) {
        this.apiID = apiID;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.coverUrl = coverUrl;
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return "Title: " + title + "\n" +
                "Author: " + author + "\n" +
                "Rating: " + rating + "\n" +
                "Description: " + description;

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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDescription() {
        //TODO: If no description, grab from the API?
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApiID() {
        return apiID;
    }

    public void setApiID(String apiID) {
        this.apiID = apiID;
    }
}
