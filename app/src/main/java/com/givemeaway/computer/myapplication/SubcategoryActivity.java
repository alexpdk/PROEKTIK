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

import com.givemeaway.computer.myapplication.AdditionalClasses.ObjectsConverter;
import com.givemeaway.computer.myapplication.AdditionalClasses.ServerParam;
import com.givemeaway.computer.myapplication.AdditionalClasses.Subcategory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SubcategoryActivity extends AppCompatActivity {

    private SubcategoryAdapter subcategoryAdapter;
    private ListView listView;
    private ArrayList<Subcategory> subcategoryArrayList;
    private SubcategoryTask subcategoryTask;
    private String id;
    private String jsonSubcategory;
    private ObjectsConverter objectsConverter;
    private OkHttpClient client;
    private ServerParam serverParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);

        serverParam = new ServerParam();
        objectsConverter = new ObjectsConverter();
        client = new OkHttpClient();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        listView = (ListView)findViewById(R.id.listViewSubcategories);

        subcategoryTask = new SubcategoryTask();
        subcategoryTask.execute();

        //Обработка кликов на итемы
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = subcategoryArrayList.get(i).getId();
                Intent intent = new Intent(getBaseContext(), PlaceActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }




    class SubcategoryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Request request = new Request.Builder().url("http://"+ serverParam.getIP()+":"+serverParam.getPort()+"/Servlet?query=GetSubcategories&category="+id).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    jsonSubcategory = "Failure!";
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    jsonSubcategory = (response.body().string());
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(jsonSubcategory == null){}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            subcategoryArrayList = objectsConverter.ConvertToSubcategoryArrayList(jsonSubcategory);
            subcategoryAdapter = new SubcategoryAdapter(getApplicationContext(), R.layout.row_subcategory, subcategoryArrayList);
            listView.setAdapter(subcategoryAdapter);
        }
    }

    public class SubcategoryAdapter extends ArrayAdapter {

        private List<Subcategory> subcategories;
        private int resource;
        private LayoutInflater inflater;
        public SubcategoryAdapter(@NonNull Context context, int resource, @NonNull List<Subcategory> objects) {
            super(context, resource, objects);
            subcategories = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.row_subcategory, null);
            }
            TextView textViewSubcategoryName;

            textViewSubcategoryName = (TextView)convertView.findViewById(R.id.textViewSubcategoryName);

            textViewSubcategoryName.setText(subcategories.get(position).getName());

            return convertView;
        }
    }
}
