package com.example.park;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DrawerLayout drawer;
    private TextView ActionBarTitle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //NAv drawer setup
        Toolbar toolbar=findViewById(R.id.toolbar);
        //action bar set current city
        ActionBarTitle=findViewById(R.id.toolbar_title);
        String currCity=getCurrCity();
        ActionBarTitle.setText(currCity);

        //continue drawer setup
        setSupportActionBar(toolbar);
        drawer=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //first fragment is our main fragment when you start app
        if (savedInstanceState==null){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AllpostsFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_allposts);}

        //Firestore db
        FirebaseApp.initializeApp(this);

        //Firebase UI-Auth
        mAuth=FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if (user!=null){
                    //User is already signed in
                    Log.i("TEST",user.getUid());
                }
                else{
                    //not signed in
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .setLogo(R.drawable.ic_launcher_foreground)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    private String getCurrCity() {
        return "Saarbrücken";
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }



    private void create_post(){
        Map<String, Object> city = new HashMap<>();
        city.put("name", "Los Angeles");
        city.put("state", "CA");
        city.put("country", "USA");

        db.collection("cities").document(mAuth.getCurrentUser().getUid())
                .set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TEST", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TEST", "Error writing document", e);
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_allposts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AllpostsFragment()).commit();
                break;
            case R.id.nav_myposts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MypostsFragment()).commit();
                break;
            case R.id.nav_help:
                Toast.makeText(this,"HELP",Toast.LENGTH_SHORT);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
    private void create_random_posts(){
        Random random=new Random();
        for (int j=0;j<50;j++){
            char c= (char) (random.nextInt(25)+65);
            char b= (char) (random.nextInt(25)+65);
            int num=random.nextInt();
            String licenseplate= String.valueOf(c+b+num);
            Map<String, Object> city = new HashMap<>();
            city.put("plate", "SB"+licenseplate);
            city.put("describtion", "Here we go again");
            city.put("email", "danyburnage@googlemail.com"+String.valueOf(num));
            city.put("phone", String.valueOf(num));
            city.put("username",mAuth.getCurrentUser().getUid());

            db.collection("posts").add(city).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("TEST","DONE");
                }
            });
        }
    }
}
