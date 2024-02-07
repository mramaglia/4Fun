package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fansfun.R;
import com.example.fansfun.adapters.EventoAdapter;
import com.example.fansfun.entities.Evento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ImageView profileImage, arrow;
    private TextView textNome, textLuogo;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        auth=FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        profileImage=findViewById(R.id.imageView);
        textNome=findViewById(R.id.textView6);
        textLuogo=findViewById(R.id.textView7);
        listView=findViewById(R.id.profileListView);
        arrow=findViewById(R.id.arrow);

        String userId = getIntent().getStringExtra("user_id");

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DocumentReference docRef = db.collection("utenti").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nome = document.getString("nome");
                        textNome.setText(nome);
                        String luogo = document.getString("luogo");
                        textLuogo.setText(luogo);
                        String imageUrl=document.getString("foto");
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .into(profileImage);
                        // Puoi utilizzare i dati qui
                    } else {
                        Log.d("Firestore", "Nessun documento trovato con questo ID");
                    }
                } else {
                    Log.d("Firestore", "Errore nel recupero del documento", task.getException());
                }
            }
        });

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

                        EventoAdapter adapter = new EventoAdapter(ProfileActivity.this, R.layout.item_evento, listaEventi);

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
                Intent intent = new Intent(ProfileActivity.this, ViewEvent.class);

                // Aggiungo id agli extra
                intent.putExtra("evento", eventoSelezionato);

                // Avvia la nuova Activity
                startActivity(intent);
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}