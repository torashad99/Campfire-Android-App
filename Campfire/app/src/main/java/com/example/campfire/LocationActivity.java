package com.example.campfire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import androidx.core.content.ContextCompat;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener {

    String locationId;
    String legend_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView locationTitle = findViewById(R.id.locationTitle);
        TextView locationDescription = findViewById(R.id.locationDescription);

        locationId = getIntent().getStringExtra("location_id");
        legend_name = getIntent().getStringExtra("legend_title");
        String description = getIntent().getStringExtra("legend_description");

        locationTitle.setText(legend_name);
        locationDescription.setText(description);



        ImageView locationImg = null;

        if (legend_name.equals("Altgeld Hall Dairy Queen")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.altgelddq);
            locationImg.setImageBitmap(bitmap);
        } else if (legend_name.equals("Steam Tunnels")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.steam_tunnels);
            locationImg.setImageBitmap(bitmap);
        } else if (legend_name.equals("McFarland Memorial Bell Tower")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bell_tower);
            locationImg.setImageBitmap(bitmap);
        } else if (legend_name.equals("Dome of Foellinger")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.foellinger);
            locationImg.setImageBitmap(bitmap);
        } else if (legend_name.equals("Bulldozer Buried under Memorial Stadium")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.memorial3_720);
            locationImg.setImageBitmap(bitmap);
        } else if (legend_name.equals("Alma Mater")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alma);
            locationImg.setImageBitmap(bitmap);
        } else if (legend_name.equals("English Building Ghost")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.english_building);
            locationImg.setImageBitmap(bitmap);
        } else if (legend_name.equals("Roger Ebert Statue")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.roger_ebert);
            locationImg.setImageBitmap(bitmap);
        } else if (legend_name.equals("Lincoln Hall")) {
            locationImg = findViewById(R.id.locationImage);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lincoln);
            locationImg.setImageBitmap(bitmap);
        }

        MaterialButton backButton = findViewById(R.id.backButton);
        MaterialButton storiesButton = findViewById(R.id.storiesButton);

        if (backButton != null) backButton.setOnClickListener(this);
        if (storiesButton != null) storiesButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            finish();
        }
        if (v.getId() == R.id.storiesButton) {
            Intent goToStories = new Intent(LocationActivity.this, PostsActivity.class);
            goToStories.putExtra("location_id", locationId);
            goToStories.putExtra("location_title", legend_name);
            LocationActivity.this.startActivity(goToStories);
        }
    }
}