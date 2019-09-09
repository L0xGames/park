package com.example.park;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FullPostFragment extends Fragment {
    private String plate,email,phone,describtion;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fullpost,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().findViewById(R.id.search_view).setVisibility(View.INVISIBLE);
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
            Log.i("KIAN",plate);
        }
    }
}
