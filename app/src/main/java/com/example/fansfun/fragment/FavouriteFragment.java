package com.example.fansfun.fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.fansfun.R;
import com.example.fansfun.activities.ListaEventi;
import com.example.fansfun.activities.MainActivity;
import com.example.fansfun.activities.ViewEvent;
import com.example.fansfun.adapters.EventoAdapter;
import com.example.fansfun.entities.ListViewEvent;
import com.example.fansfun.entities.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView listaPreferiti;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        listaPreferiti=view.findViewById(R.id.listaPreferiti);

        listaPreferiti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Ottengo l'evento selezionato dalla posizione
                ListViewEvent eventoSelezionato = (ListViewEvent) parent.getItemAtPosition(position);

                // Creo un Intent per avviare la nuova Activity
                Intent intent = new Intent(getContext(), ViewEvent.class);

                // Aggiungo id agli extra
                intent.putExtra("idEvento", eventoSelezionato.getId());

                // Avvia la nuova Activity
                startActivity(intent);
            }
        });

        return view;

    }

    public void updatePreferiti(List<ListViewEvent> listaEventi) {

        EventoAdapter adapter = new EventoAdapter(getContext(), R.layout.item_evento, listaEventi);

        // Impostare l'adattatore sulla ListView
        listaPreferiti.setAdapter(adapter);

    }

}