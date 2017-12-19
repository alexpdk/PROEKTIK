package com.givemeaway.computer.myapplication.AdditionalClasses;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Computer on 19.12.2017.
 */

public class App {
    public static void finishApp(AppCompatActivity appCompatActivity) {
        appCompatActivity.finish();
    }

    public static void refreshApp(AppCompatActivity appCompatActivity) {
        appCompatActivity.recreate();
    }
}
