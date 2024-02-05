package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.fansfun.R;
import com.example.fansfun.adapters.EventoAdapter;
import com.example.fansfun.entities.ListViewEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchEventList extends AppCompatActivity {

    ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event_list);

        listView=findViewById(R.id.searchList);

        // Dichiarazione della lista per gli eventi
        List<ListViewEvent> listaEventi = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Ottengo l'evento selezionato dalla posizione
                ListViewEvent eventoSelezionato = (ListViewEvent) parent.getItemAtPosition(position);

                // Creo un Intent per avviare la nuova Activity
                Intent intent = new Intent(SearchEventList.this, ViewEvent.class);

                // Aggiungo id agli extra
                intent.putExtra("idEvento", eventoSelezionato.getId());

                // Avvia la nuova Activity
                startActivity(intent);
            }
        });

        String searchQuery = getIntent().getStringExtra("search_query");

        // Esempio: supponiamo che tu abbia una raccolta "eventi" con documenti che contengono il campo "nome"
        CollectionReference eventiRef = db.collection("eventi");

        // Esegui la query per trovare eventi il cui campo "nome" contiene la sottostringa searchQuery
        eventiRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Elabora i documenti restituiti
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Ottieni l'oggetto Evento dal documento
                        ListViewEvent evento = document.toObject(ListViewEvent.class);
                        evento.setId(document.getId());

                        if (evento.getNome().toLowerCase().contains(searchQuery.toLowerCase())) {

                            // Aggiungi l'evento alla lista
                            listaEventi.add(evento);

                            // Creazione dell'adattatore
                            EventoAdapter adapter = new EventoAdapter(SearchEventList.this, R.layout.item_evento, listaEventi);

                            // Impostare l'adattatore sulla ListView
                            listView.setAdapter(adapter);

                        }

                    }

                    // Ora listaEventi contiene tutti gli eventi dalla collezione
                    // Puoi fare qualcosa con la lista, ad esempio visualizzarla in una ListView
                    // o passarla a un adattatore per la visualizzazione
                } else {
                    Log.d("Firestore", "Errore nel recupero degli eventi", task.getException());
                }
            }
        });

    }
}