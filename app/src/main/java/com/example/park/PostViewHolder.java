package com.example.park;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewHolder extends RecyclerView.ViewHolder {
    private TextView plateView;
    private TextView descrView;
    public LinearLayout linearLayout;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        linearLayout=itemView.findViewById(R.id.linearLayout_item);
        plateView=itemView.findViewById(R.id.license_plate_textView);
        descrView=itemView.findViewById(R.id.describtion_textView);
    }
    public void bind(Post post){
        plateView.setText(post.plate);
        descrView.setText(post.describtion);
    }
}
