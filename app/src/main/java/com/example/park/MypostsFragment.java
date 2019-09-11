package com.example.park;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MypostsFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String uid = mAuth.getCurrentUser().getUid();
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFabAdd;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mPostsCollection = db.collection("posts");
    private Query mQuery = mPostsCollection.whereEqualTo("username", uid);
    private ArrayList<Post> mAllposts;
    AllPostsAdapter allPostsAdapter;
    private ProgressBar spinner;
    private SharedPreferences appSharedPrefs;
    Activity mActivity;
    private SearchView searchView;
    private Boolean swiped = false;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Listen for changes in Datatbase
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e);
                    return;
                }
                mAllposts=new ArrayList<>();
                mAllposts.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    mAllposts.add(doc.toObject(Post.class));
                }
                //save to shared prefs
                write_prefs();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_myposts,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //add Button
        mFabAdd=getView().findViewById(R.id.fab_add_my);
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CreatePostFragment()).addToBackStack( "tag2" ).commit();
            }
        });
        //Search view
        searchView = getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                allPostsAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                allPostsAdapter.filter(newText);
                return true;
            }
        });

        //init refresh and add
        Log.i("TEST","2");
        mFabAdd = getView().findViewById(R.id.fab_add_my);
        mSwipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout_my);
        //init Progress bar
        spinner = getView().findViewById(R.id.progressBar_my);
        //init new Recycler
        mAllposts=new ArrayList<>();
        mRecyclerView = getView().findViewById(R.id.recyclerview_posts_my);
        mRecyclerView.setHasFixedSize(true);

        //SharedPref get
        SharedPreferences sharedPreferences=PreferenceManager
                .getDefaultSharedPreferences(mActivity);
        Gson gson=new Gson();
        String json=sharedPreferences.getString("SavedArray_my","");
        Type type = new TypeToken<ArrayList<Post>>(){}.getType();
        mAllposts = gson.fromJson(json, type);

        if (mAllposts != null) {
            Log.i("TEST","FIF");
            spinner.setVisibility(View.INVISIBLE);
            allPostsAdapter = new AllPostsAdapter(mAllposts);
            mRecyclerView.setAdapter(allPostsAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        } else {
            Log.i("TEST","SIF");
            spinner.setVisibility(View.VISIBLE);
            getMyFirestorePosts();
            //removing null from prefsshared
            mAllposts = new ArrayList<>();
            allPostsAdapter = new AllPostsAdapter(mAllposts);
            mRecyclerView.setAdapter(allPostsAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }


        // Refresh Action on Swipe Refresh Layout
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swiped = true;
                Log.i("ITER", "Listener triggered");
                getMyFirestorePosts();
            }
        });
    }

    private void getMyFirestorePosts() {
        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mAllposts.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mAllposts.add(document.toObject(Post.class));
                        Log.d("FIREBASE", "f");
                    }
                    allPostsAdapter.notifyDataSetChanged();
                    spinner.setVisibility(View.GONE);
                    //save to shared prefs
                    write_prefs();
                    if (swiped == true) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(mActivity, "Liste aktualisiert", Toast.LENGTH_SHORT).show();
                        swiped = false;
                    }
                } else {
                    if (swiped == true) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(mActivity, "Konnte Liste nicht aktualisieren", Toast.LENGTH_SHORT).show();
                        swiped = false;
                    }
                    Log.d("ERROR", "Error getting documents: ", task.getException());
                }

            }
        });


    }


    private void write_prefs(){
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        SharedPreferences.Editor editor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mAllposts);
        editor.putString("SavedArray_my", json);
        editor.commit();
    }
}
