package com.example.park;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AllPostsAdapter extends RecyclerView.Adapter<AllPostsAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View rowpostView = inflater.inflate(R.layout.item_post, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(rowpostView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Post post=mAllPosts.get(position);

        TextView plateView=holder.plateView;
        TextView descrView=holder.descrView;
        plateView.setText(post.plate);
        descrView.setText(post.describtion);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.plateView.getContext(),mAllPosts.get(position).plate,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAllPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView plateView;
        private TextView descrView;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout=itemView.findViewById(R.id.linearLayout_item);
            plateView=itemView.findViewById(R.id.license_plate_textView);
            descrView=itemView.findViewById(R.id.describtion_textView);
        }
    }
    private List<Post> mAllPosts,itemsCopy;

    public AllPostsAdapter(List<Post> mAllPosts) {
        this.mAllPosts = mAllPosts;
        this.itemsCopy=new ArrayList<>(mAllPosts);
    }
    public void filter(String text) {
        mAllPosts.clear();
        if(text.isEmpty()){
            mAllPosts.addAll(itemsCopy);
        } else{
            text = text.toLowerCase();
            for(Post item: itemsCopy){
                if(item.plate.toLowerCase().contains(text)){
                    mAllPosts.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
