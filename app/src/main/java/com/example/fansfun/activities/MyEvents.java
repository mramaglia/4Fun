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
import com.example.fansfun.entities.Evento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyEvents extends AppCompatActivity {

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        listView=findViewById(R.id.myEventsListView);

        FirebaseFirestore db= FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = db.collection("eventi").whereEqualTo("organizzatore", userId);

        // Esecuzione della query
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Evento> listaEventi = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Converti il documento Firestore in un oggetto Evento

                        Evento evento = document.toObject(Evento.class);
                        evento.setId(document.getId());
                        listaEventi.add(evento);

                        EventoAdapter adapter = new EventoAdapter(MyEvents.this, R.layout.item_evento, listaEventi);

                        // Impostare l'adattatore sulla ListView
                        listView.setAdapter(adapter);


                    }

                    // Ora listaEventi contiene tutti gli eventi con la categoria desiderata
                    // Puoi fare ciò che vuoi con la lista


                } else {
                    // Gestisci eventuali errori
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Ottengo l'evento selezionato dalla posizione
                Evento eventoSelezionato = (Evento) parent.getItemAtPosition(position);

                // Creo un Intent per avviare la nuova Activity
                Intent intent = new Intent(MyEvents.this, ViewEvent.class);

                // Aggiungo id agli extra
                intent.putExtra("idEvento", eventoSelezionato.getId());

                // Avvia la nuova Activity
                startActivity(intent);
            }
        });


    }
}