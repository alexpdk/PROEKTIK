package com.givemeaway.computer.myapplication.AdditionalClasses;

/**
 * Created by Computer on 09.12.2017.
 */

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class ObjectsConverter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ArrayList<Category> ConvertToCategoryArrayList(String categories){
        ArrayList<Object> jsonList = GSON.fromJson(categories, ArrayList.class);
        ArrayList<Category> catList = new ArrayList<>();
        for (int i = 0; i < jsonList.size(); i++) {
            String json = GSON.toJson(jsonList.get(i));
            Category category = GSON.fromJson(json, Category.class);
            catList.add(category);
        }
        return catList;
    }

    public ArrayList<Subcategory> ConvertToSubcategoryArrayList(String subcategories){
        ArrayList<Object> jsonList = GSON.fromJson(subcategories, ArrayList.class);
        ArrayList<Subcategory> subcatList = new ArrayList<>();
        for (int i = 0; i < jsonList.size(); i++) {
            String json = GSON.toJson(jsonList.get(i));
            Subcategory subcategory = GSON.fromJson(json, Subcategory.class);
            subcatList.add(subcategory);
        }
        return subcatList;
    }

    public ArrayList<Place> ConvertToPlaceArrayList(String places){
        ArrayList<Object> jsonList = GSON.fromJson(places, ArrayList.class);
        ArrayList<Place> placeList = new ArrayList<>();
        for (int i = 0; i < jsonList.size(); i++) {
            String json = GSON.toJson(jsonList.get(i));
            Place place = GSON.fromJson(json, Place.class);
            placeList.add(place);
        }
        return placeList;
    }
    public String ConvertToPlaceArrayJSON(ArrayList<Place> places){
        String res = GSON.toJson(places, ArrayList.class);
        Log.i("GSON toJson",res);
        return res;
    }

    public ArrayList<Review> ConvertToReviewArraylist(String reviews) {
        ArrayList<Object> jsonList = GSON.fromJson(reviews, ArrayList.class);
        ArrayList<Review> reviewList = new ArrayList<>();
        for (int i = 0; i < jsonList.size(); i++) {
            String json = GSON.toJson(jsonList.get(i));
            Review review = GSON.fromJson(json, Review.class);
            reviewList.add(review);
        }
        return reviewList;
    }
}
