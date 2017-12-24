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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.givemeaway.computer.myapplication.AdditionalClasses.DataProvider;
import com.givemeaway.computer.myapplication.AdditionalClasses.ObjectsConverter;
import com.givemeaway.computer.myapplication.AdditionalClasses.Place;
import com.givemeaway.computer.myapplication.AdditionalClasses.ServerParam;
import com.givemeaway.computer.myapplication.AdditionalClasses.Subcategory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaceActivity extends AppCompatActivity {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    //private PlaceAdapter placeAdapter;
    private ListView listView;
    private ArrayList<Place> placeArrayList;
    private ArrayList<Place> filteredList;
    private PlaceTask placeTask;
    private String id;
    private String jsonPlace;
    private ObjectsConverter objectsConverter;
    private OkHttpClient client;
    private ServerParam serverParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        placeArrayList = new ArrayList<>();
        serverParam = new ServerParam();
        objectsConverter = new ObjectsConverter();
        client = new OkHttpClient();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        listView = (ListView)findViewById(R.id.listViewPlaces);

        jsonPlace = DataProvider.getPlaces(getResources());
        placeArrayList = objectsConverter.ConvertToPlaceArrayList(jsonPlace);
        filteredList = new ArrayList<>();
        for(Place p : placeArrayList){
            if(p.getSubcategoryID().equals(id)) filteredList.add(p);
        }

        PlaceAdapter placeAdapter = new PlaceAdapter(getApplicationContext(), R.layout.row_place, filteredList);

        listView.setAdapter(placeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), OnePlaceActivity.class);
                String json = GSON.toJson(filteredList.get(i));
                intent.putExtra("json", json);
                startActivity(intent);
            }
        });
        Button showPlaces = findViewById(R.id.showPlaces);
        showPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MapActivity.class);
                intent.putExtra("json_all", new ObjectsConverter().ConvertToPlaceArrayJSON(filteredList));
                startActivity(intent);
            }
        });
    }

    class PlaceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Request request = new Request.Builder().url("http://"+ serverParam.getIP()+":"+serverParam.getPort()+"/Servlet?query=GetPlaces&subcategory="+id).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    jsonPlace = "Failure!";
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    jsonPlace = (response.body().string());
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(jsonPlace == null){}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            placeArrayList = objectsConverter.ConvertToPlaceArrayList(jsonPlace);
            PlaceAdapter placeAdapter = new PlaceAdapter(getApplicationContext(), R.layout.row_place, placeArrayList);
            listView.setAdapter(placeAdapter);
        }
    }


    public class PlaceAdapter extends ArrayAdapter {
        private Context context;
        private List<Place> places;
        private int resource;
        private LayoutInflater inflater;
        public PlaceAdapter(@NonNull Context context, int resource, @NonNull List<Place> objects) {
            super(context, resource, objects);
            this.places = objects;
            this.resource = resource;
            this.context = context ;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.row_place, null, true);
            }

            TextView textViewRowPlaceName;
            TextView textViewRowPlaceShortDescription;
            RatingBar ratingBarRowPlace;
            ImageView imageViewRowPlace;

            textViewRowPlaceName = (TextView)convertView.findViewById(R.id.textViewRowPlaceName);
            textViewRowPlaceName.setText(places.get(position).getName());

            textViewRowPlaceShortDescription = (TextView)convertView.findViewById(R.id.textViewRowPlaceShortDescription);
            textViewRowPlaceShortDescription.setText(places.get(position).getShortDescription());

            ratingBarRowPlace = (RatingBar)convertView.findViewById(R.id.ratingBarRowPlace);
            ratingBarRowPlace.setRating(places.get(position).Rating());

            imageViewRowPlace = (ImageView)convertView.findViewById(R.id.imageViewRowPlace);
            Picasso
                    .with(context)
                    .load(places.get(position).getPhotoLink())
                    .error(R.drawable.ic_highlight_off_black_24dp)
                    .placeholder(R.drawable.ic_swap_vert_black_24dp)
                    .resize(3200, 1800)
                    .into(imageViewRowPlace);
            return convertView;
        }
    }
}


