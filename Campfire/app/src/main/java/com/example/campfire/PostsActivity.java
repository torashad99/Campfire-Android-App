package com.example.campfire;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostsActivity extends AppCompatActivity implements View.OnClickListener, PostsInterface  {

    List<Post> posts = new ArrayList<>();

    String locationId;
    String locationName;

    private final String TAG = "PostsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_posts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        locationId = getIntent().getStringExtra("location_id");
        locationName = getIntent().getStringExtra("location_title");

        FloatingActionButton newPostButton = findViewById(R.id.addPostButton);
        newPostButton.setOnClickListener(this);

        MaterialButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("stories")
            .whereEqualTo("locationId", locationId)
            .get()
            .addOnCompleteListener(task -> {
                posts.clear();

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String title = (String) document.get("title");
                        String content = (String) document.get("content");

                        if (title != null && content != null && !content.isEmpty()) {
                            posts.add(new Post(title, content));
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                loadPosts();
            });
    }

    private void loadPosts() {
        PostsAdapter postAdapter = new PostsAdapter( posts, this );
        RecyclerView posts_view = findViewById(R.id.post_list);
        posts_view.setLayoutManager(new LinearLayoutManager(this));
        posts_view.setAdapter(postAdapter);

        TextView noPostsMessage = findViewById(R.id.no_posts_message);

        int noPostViewMessageVisibility = postAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE;
        noPostsMessage.setVisibility(noPostViewMessageVisibility);
    }


    public void onClick(View v) {
        if (v.getId() == R.id.addPostButton) {
            Intent createNewPost = new Intent(PostsActivity.this, NewPostActivity.class);
            createNewPost.putExtra("location_id", locationId);
            PostsActivity.this.startActivity(createNewPost);
        }
    }

    @Override
    public void onPostClick(int position) {
        Intent viewPost = new Intent(PostsActivity.this, PostActivity.class);

        Post post = posts.get(position);

        // TODO: get the location name from the activity. Continue to pass it down.
        viewPost.putExtra("Location", locationName);
        viewPost.putExtra("Title", post.title);
        viewPost.putExtra("Content", post.content);

        PostsActivity.this.startActivity(viewPost);
    }
}