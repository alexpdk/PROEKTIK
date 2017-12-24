package com.givemeaway.computer.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.givemeaway.computer.myapplication.AdditionalClasses.Category;
import com.givemeaway.computer.myapplication.AdditionalClasses.ObjectsConverter;
import com.givemeaway.computer.myapplication.AdditionalClasses.Place;
import com.givemeaway.computer.myapplication.AdditionalClasses.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

public class ViewActivity extends AppCompatActivity {

    private Button buttonFree;
    private String json;
    private ImageView imageView;
    private TextView namePlace;
    private TextView showReviews;
    private TextView description;
    private RatingBar ratingBar;
    private ObjectsConverter objectsConverter;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Intent intent = getIntent();
        json = intent.getStringExtra("json");
        final Place place = GSON.fromJson(json, Place.class);

        imageView = (ImageView)findViewById(R.id.imageViewPlace);

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Picasso
//                        .with(getBaseContext())
//                        .load(place.getPhotoLink())
//                        .into(imageView);
//            }
//        });
        Log.i("Picasso","Load "+place.getPhotoLink());
        Picasso
                .with(getBaseContext())
                .load(place.getPhotoLink())
                .error(R.drawable.ic_highlight_off_black_24dp)
                .placeholder(R.drawable.ic_swap_vert_black_24dp)
                .resize(3200, 1800)
                .into(imageView);
        namePlace = (TextView)findViewById(R.id.textViewPlaceName);
        namePlace.setText(place.getName());
        showReviews = (TextView)findViewById(R.id.textViewShowReviews);
        showReviews.setVisibility(View.INVISIBLE);
        showReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ReviewActivity.class);
                intent.putExtra("json",json);
                startActivity(intent);
            }
        });
        description = (TextView)findViewById(R.id.textViewPlaceDescription);
        description.setText(place.getDescription());
        ratingBar = (RatingBar)findViewById(R.id.ratingBarPlace);
        ratingBar.setRating(place.Rating());
        buttonFree = (Button)findViewById(R.id.buttonPlaceFree2);

        if(place.getPrice() == 0){
            Log.i("Free","Free");
            buttonFree.setVisibility(View.VISIBLE);
        }
        else {
            buttonFree.setVisibility(View.INVISIBLE);
        }
    }
}
