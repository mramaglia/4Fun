package com.example.fansfun.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fansfun.R;
import com.example.fansfun.activities.LoginActivity;
import com.example.fansfun.activities.MainActivity;
import com.example.fansfun.activities.PrincipalActivity;
import com.example.fansfun.activities.ProfileActivity;
import com.example.fansfun.entities.Utente;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private ImageView profileImg, eyesImg;
    private TextView nome, luogo, textViewLogout;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ottieni un riferimento alle SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("my_shared_pref", MODE_PRIVATE);

        // Recupera la stringa JSON dell'Utente
        String utenteJson = sharedPreferences.getString(MainActivity.KEY_USER, null);

        // Utilizza Gson per convertire la stringa JSON in un oggetto Utente
        Gson gson = new Gson();
        Utente utente = gson.fromJson(utenteJson, Utente.class);

        profileImg = view.findViewById(R.id.imageView2);
        eyesImg = view.findViewById(R.id.viewProfileEyes);
        nome = view.findViewById(R.id.textView4);
        luogo = view.findViewById(R.id.textView3);


        textViewLogout = view.findViewById(R.id.logout);

        String imageUrl = utente.getFoto();
        Glide.with(this)
                .load(imageUrl)
                .into(profileImg);

        nome.setText(utente.getNome() + " " + utente.getCognome());
        luogo.setText(utente.getLuogo());

        eyesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfile();
            }
        });

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().remove("is_authenticated").apply();
                startActivity(new Intent(getContext(), MainActivity.class));

            }
        });

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

    private void viewProfile() {
        startActivity(new Intent(getContext(), ProfileActivity.class));
    }
}
