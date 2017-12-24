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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.givemeaway.computer.myapplication.AdditionalClasses.App;
import com.givemeaway.computer.myapplication.AdditionalClasses.ObjectsConverter;
import com.givemeaway.computer.myapplication.AdditionalClasses.Place;
import com.givemeaway.computer.myapplication.AdditionalClasses.Review;
import com.givemeaway.computer.myapplication.AdditionalClasses.ServerParam;
import com.givemeaway.computer.myapplication.AdditionalClasses.Subcategory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class ReviewActivity extends AppCompatActivity {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviewArrayList = new ArrayList<>();
    private String jsonPlace;
    private ListView listView;
    private EditText editTextAuthorName;
    private EditText editTextReviewText;
    private RatingBar ratingBar;
    private Button buttonSend;
    private ObjectsConverter objectsConverter;
    private OkHttpClient client;
    private ServerParam serverParam;
    private Place place;
    private Review review;
    private String jsonReviewSend;
    boolean checkUpdate;
    private String intentUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        checkUpdate = false;

        listView = (ListView)findViewById(R.id.listViewReview);
        editTextAuthorName = (EditText)findViewById(R.id.editTextReviewAddName);
        editTextReviewText = (EditText)findViewById(R.id.editTextReviewAddText);
        ratingBar = (RatingBar)findViewById(R.id.ratingBarReviewAdd);
        buttonSend = (Button)findViewById(R.id.buttonReviewAddSend);

        serverParam = new ServerParam();
        objectsConverter = new ObjectsConverter();
        client = new OkHttpClient();
        Intent intent = getIntent();
        jsonPlace = intent.getStringExtra("json");

        place = GSON.fromJson(jsonPlace, Place.class);
        reviewArrayList = place.getReviews();
        reviewAdapter = new ReviewAdapter(getApplicationContext(), R.layout.row_review, reviewArrayList);
        listView.setAdapter(reviewAdapter);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingBar.setRating(v);
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                review = new Review(place.getId(), -1, editTextAuthorName.getText().toString(), editTextReviewText.getText().toString(), ratingBar.getRating(), "");
                jsonReviewSend = GSON.toJson(review);
                new ReviewSendTask().execute();
            }
        });
    }

    class ReviewSendTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RequestBody body = new FormBody.Builder()
                    .add("query", "AddReview")
                    .add("review", jsonReviewSend)
                    .build();
            final Request request = new Request.Builder()
                    .url("http://"+ serverParam.getIP()+":"+serverParam.getPort()+"/Servlet")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(getApplicationContext(), "Не удалось добавить отзыв", Toast.LENGTH_SHORT);
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    checkUpdate = true;
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(checkUpdate == false){}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            startActivity(getIntent());
            finish();
        }
    }

    public void UpdateApp() {
        App.refreshApp(this);
    }



    class ReviewTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            int id = -1;
            final Request request = new Request.Builder().url("http://"+ serverParam.getIP()+":"+serverParam.getPort()+"/Servlet?query=GetPlace&placeID="+id).build();
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
            place = GSON.fromJson(jsonPlace, Place.class);
            reviewArrayList = place.getReviews();
            reviewAdapter = new ReviewAdapter(getApplicationContext(), R.layout.row_review, reviewArrayList);
            listView.setAdapter(reviewAdapter);
        }
    }

    public class ReviewAdapter extends ArrayAdapter {

        private List<Review> reviews;
        private int resource;
        private LayoutInflater inflater;
        public ReviewAdapter(@NonNull Context context, int resource, @NonNull List<Review> objects) {
            super(context, resource, objects);
            reviews = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.row_review, null);
            }
            TextView textViewAuthorName;
            //TextView textViewDate;
            TextView textViewReviewText;
            RatingBar ratingBar;

            textViewAuthorName = (TextView)convertView.findViewById(R.id.textViewReviewRowAuthor);
            textViewAuthorName.setText(reviews.get(position).getAuthor());

            //textViewDate = (TextView)convertView.findViewById(R.id.textViewReviewRowDate);
            //textViewDate.setText(reviews.get(position).getDate());

            textViewReviewText = (TextView)convertView.findViewById(R.id.textViewReviewRowText);
            textViewReviewText.setText(reviews.get(position).getText());

            ratingBar = (RatingBar)convertView.findViewById(R.id.ratingBarReviewRow);
            ratingBar.setRating(reviews.get(position).getMark());

            return convertView;
        }
    }
}
