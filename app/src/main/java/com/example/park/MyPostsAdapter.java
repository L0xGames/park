package com.example.park;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

//create different Adapter to use different row-Layout
public class MyPostsAdapter extends AllPostsAdapter{

    public MyPostsAdapter(List<Post> mAllPosts) {
        super(mAllPosts);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View rowpostView = inflater.inflate(R.layout.item_posts_delete, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(rowpostView);
        return viewHolder;
    }

}