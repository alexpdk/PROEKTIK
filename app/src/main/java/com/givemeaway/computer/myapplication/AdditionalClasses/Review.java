package com.givemeaway.computer.myapplication.AdditionalClasses;

/**
 * Created by Computer on 09.12.2017.
 */

public class Review {
    private int placeID;
    private int reviewID;
    private String author;
    private float mark;
    private String text;
    private String date;

    public Review(int placeID, int reviewID, String author, String text, float mark, String date) {
        this.placeID = placeID;
        this.reviewID = reviewID;
        this.author = author;
        this.text = text;
        this.mark = mark;
        this.date = date;
    }

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getMark() {
        return mark;
    }

    public void setMark(float mark) {
        this.mark = mark;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
