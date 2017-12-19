package com.givemeaway.computer.myapplication.AdditionalClasses;

/**
 * Created by Computer on 09.12.2017.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Place {
    @SerializedName("_id")
    private int id;
    private String name;
    private String categoryID;
    private String subcategoryID;
    private String shortDescription;
    private String description;
    private double nCoordinate;
    private double eCoordinate;
    private String photoLink;
    private double price;
    private String openingTime;
    private String closingTime;
    private ArrayList<Review> reviews;
    private String available;

    public Place(int id, String name, String shortDescription, String description, double nCoordinate, double eCoordinate, String photoLink, double price, String openingTime, String closingTime, ArrayList<Review> reviews, String subcategoryID, String categoryID, String available) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.description = description;
        this.nCoordinate = nCoordinate;
        this.eCoordinate = eCoordinate;
        this.photoLink = photoLink;
        this.price = price;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.reviews = reviews;
        this.subcategoryID = subcategoryID;
        this.categoryID = categoryID;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getnCoordinate() {
        return nCoordinate;
    }

    public void setnCoordinate(double nCoordinate) {
        this.nCoordinate = nCoordinate;
    }

    public double geteCoordinate() {
        return eCoordinate;
    }

    public void seteCoordinate(double eCoordinate) {
        this.eCoordinate = eCoordinate;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public String getSubcategoryID() {
        return subcategoryID;
    }

    public void setSubcategoryID(String subcategoryID) {
        this.subcategoryID = subcategoryID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public void addReview(ArrayList<Review> reviews, Review review) {
        this.reviews.add(review);
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public float Rating() {
        int size = this.reviews.size();
        if(size == 0){
            return 0;
        }
        else{
            float sum = 0;
            for(int i = 0; i<size; i++){
                sum += this.reviews.get(i).getMark();
            }
            return  (sum/size);
        }

    }
}
