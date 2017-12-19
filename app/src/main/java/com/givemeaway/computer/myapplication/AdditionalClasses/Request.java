package com.givemeaway.computer.myapplication.AdditionalClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;


public class Request {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private String requestType;
    private ArrayList<Parameter> params;

    public Request(String requestType) {
        this.requestType = requestType;
        this.params = new ArrayList<>();
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public ArrayList<Parameter> getParams() {
        return params;
    }

    public void addParam (String key, String value){
        Parameter parameter = new Parameter(key, value);
        params.add(parameter);
    }

    public String getRequest() {
        return GSON.toJson(this);
    }
}
