package com.example.campfire;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private final List<Post> localDataSet;

    private final PostsInterface postsInterface;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView postContent;
        private final TextView postTitle;

        public ViewHolder(View view, PostsInterface postsInterface) {
            super(view);
            // Define click listener for the ViewHolder's View

            postContent = view.findViewById(R.id.postContent);
            postTitle = view.findViewById(R.id.postTitle);

            itemView.setOnClickListener(v -> {
                if (postsInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        postsInterface.onPostClick(pos);
                    }
                }
            });
        }

        public TextView getPostContent() {
            return postContent;
        }

        public TextView getPostTitle() {
            return postTitle;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public PostsAdapter(List<Post> dataSet, PostsInterface postsInterface) {

        this.localDataSet = dataSet;
        this.postsInterface = postsInterface;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_card, viewGroup, false);

        return new ViewHolder(view, postsInterface);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        Post story = localDataSet.get(position);

        viewHolder.getPostContent().setText(story.content);

        if (story.title.isEmpty())
        {
            viewHolder.getPostTitle().setVisibility(View.GONE);
        }
        else
        {
            viewHolder.getPostTitle().setText(story.title);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
