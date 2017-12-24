package com.givemeaway.computer.myapplication.AdditionalClasses;

import android.content.res.Resources;

import com.givemeaway.computer.myapplication.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by дом on 24/12/2017.
 */

public class DataProvider {

    private static String getJSONString(int resourceCode, Resources resources){
        InputStream is = resources.openRawResource(resourceCode);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                is.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return writer.toString();
    }
    public static String getCategories(Resources resources){
        return getJSONString(R.raw.categories, resources);
    }
    public static String getSubcategories(Resources resources){
        return getJSONString(R.raw.subcategories, resources);
    }
    public static String getPlaces(Resources resources){
        return getJSONString(R.raw.locations, resources);
    }
}
