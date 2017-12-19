package com.givemeaway.computer.myapplication.AdditionalClasses;

/**
 * Created by Computer on 09.12.2017.
 */

public class Subcategory {
    private String id;
    private String name;
    private String categoryID;
    private String photoLink;

    public Subcategory(String id, String name, String categoryID, String photoLink) {
        this.id = id;
        this.name = name;
        this.categoryID = categoryID;
        this.photoLink = photoLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}