package com.example.campfire;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());


        TextView locationTitle = findViewById(R.id.locationName);
        TextView postTitle = findViewById(R.id.postTitle);
        TextView postContent = findViewById(R.id.postContent);

        String title_text = getIntent().getStringExtra("Title");
        String content_text = getIntent().getStringExtra("Content");
        String location_text = getIntent().getStringExtra("Location");

        assert title_text != null;
        if (title_text.isEmpty()) {
            postTitle.setVisibility(TextView.GONE);
        }

        postTitle.setText(title_text);
        postContent.setText(content_text);
        locationTitle.setText(location_text);
    }
}