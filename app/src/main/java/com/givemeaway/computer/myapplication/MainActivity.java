package com.givemeaway.computer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.givemeaway.computer.myapplication.AdditionalClasses.Category;
import com.givemeaway.computer.myapplication.AdditionalClasses.DataProvider;
import com.givemeaway.computer.myapplication.AdditionalClasses.ObjectsConverter;
import com.givemeaway.computer.myapplication.AdditionalClasses.ServerParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    CategoryAdapter categoryAdapter;
    private CategoryTask categoryTask;
    private ListView listView;
    private ObjectsConverter objectsConverter;
    //private Request req ;
    private ArrayList<Category> categoryArrayList;
    private OkHttpClient client;
    private String jsonCategory;
    private ServerParam serverParam;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverParam = new ServerParam();
        objectsConverter = new ObjectsConverter();
        client = new OkHttpClient();

        listView = findViewById(R.id.listViewCategories);

//        categoryTask = new CategoryTask();
//        categoryTask.execute();

        jsonCategory = DataProvider.getCategories(getResources());
        categoryArrayList = objectsConverter.ConvertToCategoryArrayList(jsonCategory);
        categoryAdapter = new CategoryAdapter(getApplicationContext(), R.layout.row_category, categoryArrayList);
        listView.setAdapter(categoryAdapter);

        //Обработка кликов на итемы
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = categoryArrayList.get(i).getId();
                Intent intent = new Intent(getBaseContext(), SubcategoryActivity.class);
                intent.putExtra("id", id);
                startActivity(intent );
            }
        });
    }


    class CategoryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Request request = new Request.Builder().url("http://"+ serverParam.getIP()+":"+serverParam.getPort()+"/Servlet?query=GetCategories").build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    jsonCategory = "Failure!";
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    jsonCategory = (response.body().string());
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(jsonCategory == null){}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            categoryArrayList = objectsConverter.ConvertToCategoryArrayList(jsonCategory);
            categoryAdapter = new CategoryAdapter(getApplicationContext(), R.layout.row_category, categoryArrayList);
            listView.setAdapter(categoryAdapter);
        }
    }

    public class CategoryAdapter extends ArrayAdapter {

        private List<Category> categories;
        private int resource;
        private LayoutInflater inflater;
        public CategoryAdapter(@NonNull Context context, int resource, @NonNull List<Category> objects) {
            super(context, resource, objects);
            categories = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.row_category, null);
            }
            TextView textViewCategoryName;

            textViewCategoryName = (TextView)convertView.findViewById(R.id.textViewCategoryName);

            textViewCategoryName.setText(categories.get(position).getName());

            return convertView;
        }
    }
}
