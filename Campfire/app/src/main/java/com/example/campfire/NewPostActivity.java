package com.example.campfire;

// import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityResultLauncher<Intent> someActivityResultLauncher;

    String locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        locationId = getIntent().getStringExtra("location_id");

        MaterialButton submitPostButton = findViewById(R.id.submitPostButton);
        MaterialButton cancelButton = findViewById(R.id.cancelButton);

        TextInputEditText captionField = findViewById(R.id.content_text_input);

        captionField.setMinLines(3);

        if (submitPostButton != null) submitPostButton.setOnClickListener(this);
        if (cancelButton != null) cancelButton.setOnClickListener(this);
    }

    private void confirmCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Discard", (dialog, id) -> finish());
        builder.setNegativeButton("Cancel",  (dialog, id) -> {});
        // Set other dialog properties.
        builder.setTitle("Discard post?");

        // Create the AlertDialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Submit", (dialog, id) -> submitPost());
        builder.setNegativeButton("Cancel", (dialog, id) -> {});
        // Set other dialog properties.
        builder.setTitle("Submit new post?");

        LayoutInflater inflater = getLayoutInflater();
        View postPreview = inflater.inflate(R.layout.post_card, null);

        TextView previewTitle = postPreview.findViewById(R.id.postTitle);
        TextView previewContent = postPreview.findViewById(R.id.postContent);

        String title = ((TextView) findViewById(R.id.title_text_input)).getText().toString();
        String content = ((TextView) findViewById(R.id.content_text_input)).getText().toString();

        previewTitle.setText(title);
        previewContent.setText(content);

        int padding_px = (int) (20 * getResources().getDisplayMetrics().density);
        postPreview.setPadding(padding_px, padding_px, padding_px, padding_px);

        builder.setView(postPreview);

        // Create the AlertDialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitPost() {
        String title = ((TextView) findViewById(R.id.title_text_input)).getText().toString();
        String content = ((TextView) findViewById(R.id.content_text_input)).getText().toString();

        Post newPost = new Post(title, content, locationId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stories").add(newPost);

        finish();
    }


    public void onClick(View v) {
        // Get the text from the caption field
        TextInputEditText captionField = findViewById(R.id.content_text_input);
        assert captionField != null;
//        Editable captionEdit = captionField.getText();
//        assert captionEdit != null;
//        String captionText = captionEdit.toString();

        if (v.getId() == R.id.submitPostButton) {
            /* TODO:
                Read the post content and store it somewhere to display in the list.
                Validate that the caption is filled out.
            */
            if (captionField.length() == 0) {
                captionField.setError("Caption required");
                return;
            }

            confirmSubmit();
        }

        if (v.getId() == R.id.cancelButton) {
            if (captionField.length() != 0) {
                confirmCancel();
            } else {
                finish();
            }
        }
    }
}