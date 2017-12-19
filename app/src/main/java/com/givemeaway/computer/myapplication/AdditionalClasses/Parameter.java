package com.givemeaway.computer.myapplication.AdditionalClasses;

/**
 * Created by Computer on 12.12.2017.
 */

public class Parameter {
    String[] parameter = new String[2];

    public Parameter(String key, String value) {
        this.parameter[0] = key;
        this.parameter[1] = value;
    }

    public void setParameter(String key, String value) {
        this.parameter[0] = key;
        this.parameter[1] = value;
    }

    public String[] getParameter() {
        return this.parameter;
    }
}
