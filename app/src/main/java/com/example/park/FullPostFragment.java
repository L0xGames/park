package com.example.park;

import android.graphics.Color;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FullPostFragment extends Fragment {
    private String plate,email,phone,describtion;
    private TextView plate_view,phone_view,email_view,descr_view;
    private FrameLayout frameLayout;
    private String[] colors={"#ff0099cc","#f4b400","#FFD81B60","#ffaa66cc","#ffff4444","#FF008577"};
    private Random random=new Random();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fullpost,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views
        frameLayout=getView().findViewById(R.id.framelayout_rand);
        getActivity().findViewById(R.id.search_view).setVisibility(View.INVISIBLE);
        plate_view=getView().findViewById(R.id.full_platetextview);
        phone_view=getView().findViewById(R.id.full_phonetextview);
        email_view=getView().findViewById(R.id.full_emailtextview);
        descr_view=getView().findViewById(R.id.full_describtiontextview);
        //random colour
        String new_color=colors[random.nextInt(colors.length)];
        Log.i("COLOR",new_color);
        frameLayout.setBackgroundColor(Color.parseColor(new_color));
        //assign everything
        plate_view.setText(plate);
        descr_view.setText(describtion);
        phone_view.setText("Mobil: "+phone);
        Linkify.addLinks(phone_view,Linkify.PHONE_NUMBERS);
        email_view.setText("Email: "+email);
        Linkify.addLinks(email_view,Linkify.EMAIL_ADDRESSES);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.search_view).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            plate=bundle.getString("plate");
            phone=bundle.getString("phone");
            describtion=bundle.getString("describtion");
            email=bundle.getString("email");
        }
    }
}
