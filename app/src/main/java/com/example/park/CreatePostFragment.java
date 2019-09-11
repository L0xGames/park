package com.example.park;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class CreatePostFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String uid = mAuth.getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText plate_input;
    private EditText phone_input;
    private EditText descr_input;
    private EditText email_input;
    private Button save_btn;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views
        plate_input=getView().findViewById(R.id.plate_editText);
        descr_input=getView().findViewById(R.id.descr_editText);
        phone_input=getView().findViewById(R.id.editText_phone);
        email_input=getView().findViewById(R.id.editText_email);
        save_btn=getView().findViewById(R.id.button_savepost);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //email parse
                String pattern="(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
                // Create a Pattern object
                Pattern r = Pattern.compile(pattern);
                // Now create matcher object.
                Matcher m = r.matcher(email_input.getText().toString());
                if(((!phone_input.getText().toString().matches(""))||(!email_input.getText().toString().matches("")))&&m.find( ))
                {
                    // not null not empty at least one of them
                    writeFirestore();
                }
                else if (!m.find()){
                    //not valid email
                    Toast.makeText(getActivity(),"Falsches E-Mail Format",Toast.LENGTH_LONG).show();
                }
                else {
                    //null or empty both
                    Toast.makeText(getActivity(),"Gib mindestens eine Kontaktmöglichkeit an ;)",Toast.LENGTH_LONG).show();
                }


            }
        });


    }
    private void writeFirestore(){
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("plate", plate_input.getText().toString());
        user.put("describtion", descr_input.getText().toString());
        user.put("phone", phone_input.getText().toString());
        user.put("email", email_input.getText().toString());
        user.put("username", uid);

        // Add a new document with a generated ID
        db.collection("posts").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getActivity(),"Kennzeichen gespeichert!",Toast.LENGTH_LONG).show();
                getFragmentManager().popBackStackImmediate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Kennzeichen konnte nicht gespeichert werden!",Toast.LENGTH_LONG).show();
            }
        });
    }
}
