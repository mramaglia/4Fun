package com.example.fansfun.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.fansfun.activities.MainActivity;
import com.example.fansfun.activities.ViewEvent;
import com.example.fansfun.entities.Utente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

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

        // Ottieni un riferimento alle SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE);

        // Recupera la stringa JSON dell'Utente
        String utenteJson = sharedPreferences.getString(MainActivity.KEY_USER, null);

        // Utilizza Gson per convertire la stringa JSON in un oggetto Utente
        Gson gson = new Gson();
        Utente utente = gson.fromJson(utenteJson, Utente.class);

        profileImg=view.findViewById(R.id.imageView2);
        eyesImg=view.findViewById(R.id.viewProfileEyes);
        nome=view.findViewById(R.id.textView4);
        luogo=view.findViewById(R.id.textView3);

        String imageUrl = utente.getFoto();
        Glide.with(this)
                .load(imageUrl)
                .into(profileImg);

        nome.setText(utente.getNome()+" "+utente.getCognome());
        luogo.setText(utente.getLuogo());

        return view;
    }

    public void updateUserProfile(Utente utente) {
        // Aggiorna l'UI con i dati dell'utente
        String imageUrl = utente.getFoto();
        Glide.with(this)
                .load(imageUrl)
                .into(profileImg);

        nome.setText(utente.getNome() + " " + utente.getCognome());
        luogo.setText(utente.getLuogo());
    }
}