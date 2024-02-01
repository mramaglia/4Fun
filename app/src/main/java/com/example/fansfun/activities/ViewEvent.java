package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fansfun.R;
import com.example.fansfun.entities.Evento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewEvent extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    ImageView imageView;

    TextView nome, descrizione, partecipanti, luogo, data, ora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        imageView=findViewById(R.id.imageView);
        nome=findViewById(R.id.textView9);
        descrizione=findViewById(R.id.textView10);
        partecipanti=findViewById(R.id.numPartecipanti);
        luogo=findViewById(R.id.textView14);
        data=findViewById(R.id.eventDate);
        ora=findViewById(R.id.eventHour);

        auth=FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        CollectionReference eventiCollection = db.collection("eventi");

        String idEvento= getIntent().getStringExtra("idEvento");

        eventiCollection.document(idEvento)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Il documento esiste, puoi ottenere i dati
                                Evento evento = document.toObject(Evento.class);
                                String imageUrl=evento.getFoto();
                                Glide.with(ViewEvent.this)
                                        .load(imageUrl)
                                        .into(imageView);

                                nome.setText(evento.getNome());
                                descrizione.setText(evento.getDescrizione());;
                                partecipanti.setText(String.valueOf(evento.getNumPartecipanti()));
                                luogo.setText(evento.getLuogo());
                                data.setText(formatData(evento.getData()));
                                ora.setText(formatOra(evento.getData()));


                            } else {
                                Log.d("Firestore", "Nessun documento trovato con ID: " + idEvento);
                            }
                        } else {
                            Log.e("Firestore", "Errore durante la ricerca del documento", task.getException());
                        }
                    }
                });




    }

    private String formatData(Date data) {
        // Formattare la data come desiderato
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }

    private String formatOra(Date data) {
        // Formattare la data come desiderato
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(data);
    }
}