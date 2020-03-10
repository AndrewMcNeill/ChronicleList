package ca.andrewmcneill.chroniclelist.beans;

public class Book {
    private String title;
    private String author;
    private double rating;
    private String coverUrl;

    public Book(String title, String author, double rating, String coverUrl) {
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.coverUrl = coverUrl;
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
}
