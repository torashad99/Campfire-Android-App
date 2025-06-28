package com.example.campfire;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gMap;
    private View fabAddLegend;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private Marker selectedMarker;
    private Snackbar initialSnackbar;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final float DEFAULT_ZOOM = 12f;
    private static final float SELECTED_ZOOM = 15f;

    private final Map<String, Legend> legends = new HashMap<>();
    private Legend selected_legend = null;
    private String selected_legend_id;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply system insets for a fullscreen layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        mapView = findViewById(R.id.mapView);
        fabAddLegend = findViewById(R.id.fabAddLegend);
        LinearLayout legendDescriptionBar = findViewById(R.id.legendDescriptionBar);
        Button btnViewDetails = findViewById(R.id.btnViewDetails);

        // Initialize BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(legendDescriptionBar);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        showInitialSnackbar();

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN); //TODO: Will need to remove to add more info, this is stopgap behavior
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        resetSelectedMarker();
                        adjustFabPosition(BottomSheetBehavior.STATE_HIDDEN); // Restored
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        initialSnackbar.dismiss();
                        adjustFabPosition(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Not needed for this implementation
            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        btnViewDetails.setOnClickListener(v -> {
            // Handle "View Details" button click here (add implementation as needed)
            Intent viewLocation = new Intent(MainActivity.this, LocationActivity.class);
            viewLocation.putExtra("location_id", selected_legend_id);
            viewLocation.putExtra("legend_title", selected_legend.title);
            viewLocation.putExtra("legend_description", selected_legend.description);

            MainActivity.this.startActivity(viewLocation);
        });

        fabAddLegend.setOnClickListener(v -> {
            // Handle FAB click (e.g., add a new legend)
            Intent addNewLocation = new Intent(MainActivity.this, NewLocationActivity.class);
            MainActivity.this.startActivity(addNewLocation);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    resetSelectedMarker();
                } else {
                    finish(); // Exit the app
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // Apply custom style to the map
        try {
            boolean success = gMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Log.e("MapStyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapStyle", "Can't find style. Error: ", e);
        }

        // Set the map's camera position to Champaign
        LatLng champaign = new LatLng(40.1164, -88.2434); // Center of Champaign
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(champaign)
                .zoom(12)  // Set zoom level to make the map look better
                .build();
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("legends")
                .get()
                .addOnCompleteListener(task -> {
//                  legends.clear(); // TODO: uncomment

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Legend legend = document.toObject(Legend.class);
                            legends.put(document.getId(), legend);

                            LatLng location = new LatLng(legend.location.getLatitude(), legend.location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location)
                                    .title(legend.title)
                                    .icon(getBitmapDescriptor(legend.getType(), false));

                            Marker marker = gMap.addMarker(markerOptions);
                            marker.setTag(document.getId());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        // Set up marker click listener
        gMap.setOnMarkerClickListener(this::onMarkerClick);
    }

    private boolean onMarkerClick(Marker marker) {
        if (selectedMarker != null && selectedMarker.equals(marker)) {
            // If the same marker is clicked again, do nothing
            return true;
        }

        resetSelectedMarker();

        // Update the selected marker
        selectedMarker = marker;
        String legendId = (String) marker.getTag();
        Legend legend = legends.get(legendId);
        selectedMarker.setIcon(getBitmapDescriptor(legend.getType(), true));

        // Center the map on the selected marker
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), SELECTED_ZOOM));

        // Show legend description
        showLegendDescription(marker);
        return true;
    }

    private void resetSelectedMarker() {
        if (selectedMarker != null) {
            String legendId = (String) selectedMarker.getTag();
            Legend legend = legends.get(legendId);
            selectedMarker.setIcon(getBitmapDescriptor(legend.getType(), false));
            selectedMarker = null;
        }
        adjustFabPosition(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void showInitialSnackbar() {
        View rootView = findViewById(android.R.id.content);
        initialSnackbar = Snackbar.make(rootView, "Click on a Pin to know more about the urban legend!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Close", v -> initialSnackbar.dismiss())
                .setAnchorView(fabAddLegend);

        initialSnackbar.show();
    }

    private void showLegendDescription(Marker marker) {
        TextView initialText = findViewById(R.id.initialText);
        TextView legendDescription = findViewById(R.id.legendDescription);
        TextView legendTitle = findViewById(R.id.legendTitle);
        Button btnViewDetails = findViewById(R.id.btnViewDetails);

        initialText.setVisibility(View.GONE);
        legendDescription.setVisibility(View.VISIBLE);
        btnViewDetails.setVisibility(View.VISIBLE);

        selected_legend_id = (String) marker.getTag();
        selected_legend = legends.get(selected_legend_id);

        legendDescription.setText(selected_legend.getDescription());
        legendTitle.setText(selected_legend.getTitle());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        adjustFabPosition(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void adjustFabPosition(int bottomSheetState) {
        if (bottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
            fabAddLegend.setVisibility(View.GONE);
        } else if (bottomSheetState == BottomSheetBehavior.STATE_HIDDEN) {
            fabAddLegend.setVisibility(View.VISIBLE);
        }
    }

    private BitmapDescriptor getBitmapDescriptor(String type, boolean isSelected) {
        String iconName = "ic_" + type.toLowerCase();
        int resourceId = getResources().getIdentifier(iconName, "drawable", getPackageName());

        if (resourceId != 0) {
            Drawable drawable = ContextCompat.getDrawable(this, resourceId);
            if (drawable instanceof VectorDrawable) {
                int width = isSelected ? 340 : 180;
                int height = isSelected ? 355 : 195;
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return BitmapDescriptorFactory.fromBitmap(bitmap);
            }
        }

        // Fallback to default marker if icon not found
        return BitmapDescriptorFactory.defaultMarker();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
