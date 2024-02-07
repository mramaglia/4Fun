package com.example.fansfun.fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.fansfun.R;
import com.example.fansfun.activities.ViewEvent;
import com.example.fansfun.adapters.EventoAdapter;
import com.example.fansfun.entities.Evento;
import com.google.firebase.firestore.FirebaseFirestore;

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
                Evento eventoSelezionato = (Evento) parent.getItemAtPosition(position);

                // Creo un Intent per avviare la nuova Activity
                Intent intent = new Intent(getContext(), ViewEvent.class);

                // Aggiungo id agli extra
                intent.putExtra("evento", eventoSelezionato);

                // Avvia la nuova Activity
                startActivity(intent);
            }
        });

        return view;

    }

    public void updatePreferiti(List<Evento> listaEventi) {

        EventoAdapter adapter = new EventoAdapter(getContext(), R.layout.item_evento, listaEventi);

        // Impostare l'adattatore sulla ListView
        listaPreferiti.setAdapter(adapter);

    }

}