package com.example.campfire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


public class NewLocationActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Spinner spinner = findViewById(R.id.legend_spinner);
        TextInputEditText descriptionField = findViewById(R.id.description_text_input);
        descriptionField.setMinLines(3);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.urbanLegendTypes,
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);

        // Set up the upload button
        MaterialButton uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> {
            // openFilePicker();
            openGallery();
        });

        MaterialButton cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> onCancelPressed());

        someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        displaySelectedImage(imageUri);
                    }
                });

        MaterialButton submitPostButton = findViewById(R.id.submitPostButton);
        submitPostButton.setOnClickListener(v -> onSubmitPressed());
    }

    private void confirmSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewLocationActivity.this);
        builder.setMessage("Are you sure you want to submit?");
        builder.setTitle("Location submission");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(NewLocationActivity.this, NewLocationConfirmationActivity.class);
            startActivity(intent);
            dialog.dismiss();
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void confirmCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Discard", (dialog, id) -> finish());
        builder.setNegativeButton("Cancel",  (dialog, id) -> {});
        // Set other dialog properties.
        builder.setTitle("Discard new location submission?");

        // Create the AlertDialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }
    private void displaySelectedImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSubmitPressed() {
        TextInputEditText descriptionField = findViewById(R.id.description_text_input);

        if (descriptionField.length() == 0) {
            descriptionField.setError("Description required");
            return;
        }

        confirmSubmit();
    }

    private void onCancelPressed() {
        TextInputEditText descriptionField = findViewById(R.id.description_text_input);

        if (descriptionField.length() != 0) {
            confirmCancel();
        } else {
            finish();
        }
    }

    private void openFilePicker() {
        Intent iGallery = new Intent(Intent.ACTION_PICK);
//        iGallery.setType("image/*");
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                    }
                }
            });
        someActivityResultLauncher.launch(iGallery);
    }

}
