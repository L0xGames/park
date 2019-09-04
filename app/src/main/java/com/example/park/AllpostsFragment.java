package com.example.park;

import android.app.LauncherActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class AllpostsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFabAdd;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mPostsCollection=db.collection("posts");
    private Query mQuery=mPostsCollection.orderBy("plate", Query.Direction.DESCENDING);
    private FirestorePagingAdapter<Post,PostViewHolder> mAdapter;
    private HashMap<Integer,Post> postslist=new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        get_documents_size();
        return inflater.inflate(R.layout.fragment_allposts,container,false);
    }

    private void get_documents_size() {
        db.collection("posts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                        }else{
                            Log.w("ERROR", "Error getting documents.", task.getException());

                        }
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init refresh and add
        mFabAdd = getView().findViewById(R.id.fab_add);
        mSwipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout);

        //init Recyclerview
        mRecyclerView=getView().findViewById(R.id.recyclerview_posts);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();

        // Refresh Action on Swipe Refresh Layout
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refresh();
            }
        });
    }

    private void setupAdapter() {
        //init Page Config
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(10)
                .build();
        // Init Adapter Configuration
        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Post.class)
                .build();

        //TEST

        //TEST ENDE

        // Instantiate Paging Adapter
        mAdapter=new FirestorePagingAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int i, @NonNull final Post post) {
                final int position=i;
                postslist.put(position,post);
                postViewHolder.bind(post);
                postViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Post e=postslist.get(position);
                        Log.i("ITER",String.valueOf(position));
                        Log.i("ITER","---------------");
                        Log.i("ITER",String.valueOf(postslist.size()));
                        if (e!=null){
                            Toast.makeText(getContext(),e.plate,Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(getContext(),"is null LOL",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=getLayoutInflater().inflate(R.layout.item_post,parent,false);
                return new PostViewHolder(view);
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Log.e("MainActivity", e.getMessage());
            }
            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        Toast.makeText(
                                getActivity(),
                                "Error Occurred!",
                                Toast.LENGTH_SHORT
                        ).show();

                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        };
        //finally set adapter to mRecyclerView
        mRecyclerView.setAdapter(mAdapter);
    }
}
