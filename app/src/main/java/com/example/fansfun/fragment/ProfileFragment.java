package com.example.fansfun.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fansfun.R;
import com.example.fansfun.activities.ViewEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private ImageView profileImg, eyesImg;
    private TextView nome, luogo;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

       // View view = inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth= FirebaseAuth.getInstance();
        profileImg=view.findViewById(R.id.imageView2);
        eyesImg=view.findViewById(R.id.viewProfileEyes);
        nome=view.findViewById(R.id.textView4);
        luogo=view.findViewById(R.id.textView3);

        String userId = auth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        DocumentReference userDocument = db.collection("utenti").document(userId);
        userDocument.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Il documento esiste, puoi ottenere i dati
                        nome.setText(documentSnapshot.getString("nome")+documentSnapshot.getString("cognome"));
                        luogo.setText(documentSnapshot.getString("luogo"));
                        String imageUrl=documentSnapshot.getString("foto");
                        Glide.with(this)
                                .load(imageUrl)
                                .into(profileImg);
                    } else {
                        // Il documento non esiste
                    }
                })
                .addOnFailureListener(e -> {
                    // Gestisci eventuali errori nella query
                    Log.e("Firestore", "Errore nella query: " + e.getMessage());
                });


        return view;
    }
}