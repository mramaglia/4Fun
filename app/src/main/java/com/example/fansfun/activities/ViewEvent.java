package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.example.fansfun.R;
import com.example.fansfun.entities.Evento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewEvent extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ImageView imageView, iscriviti, heart, back;
    private TextView nome, descrizione, partecipanti, luogo, data, ora;
    Boolean isEventFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));

        // Ottieni il riferimento al MapView dal layout
        MapView mapView = findViewById(R.id.mapView);
        imageView = findViewById(R.id.imageView);
        nome = findViewById(R.id.textView9);
        descrizione = findViewById(R.id.textView10);
        partecipanti = findViewById(R.id.numPartecipanti);
        luogo = findViewById(R.id.textView14);
        data = findViewById(R.id.eventDate);
        ora = findViewById(R.id.eventHour);
        iscriviti = findViewById(R.id.imageView6);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        CollectionReference eventiCollection = db.collection("eventi");
        Geocoder geocoder = new Geocoder(this);

        String idEvento = getIntent().getStringExtra("idEvento");



        iscriviti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iscriviti(idEvento, auth);
            }
        });

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
                                String imageUrl = evento.getFoto();
                                Glide.with(ViewEvent.this)
                                        .load(imageUrl)
                                        .into(imageView);

                                nome.setText(evento.getNome());
                                descrizione.setText(evento.getDescrizione());
                                partecipanti.setText(String.valueOf(evento.getNumPartecipanti()));
                                luogo.setText(evento.getIndirizzo() + ", " + evento.getLuogo());
                                data.setText(formatData(evento.getData()));
                                ora.setText(formatOra(evento.getData()));

                                // Imposta il livello di zoom iniziale
                                mapView.getController().setZoom(15.0);

                                String locationName = evento.getIndirizzo() + evento.getLuogo(); // Sostituisci con il tuo indirizzo o comune
                                try {
                                    List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

                                    if (!addresses.isEmpty()) {
                                        // Ottieni la prima corrispondenza di indirizzo
                                        Address address = addresses.get(0);

                                        // Ottieni le coordinate geografiche
                                        double latitude = address.getLatitude();
                                        double longitude = address.getLongitude();

                                        // Crea un oggetto GeoPoint con le coordinate
                                        GeoPoint location = new GeoPoint(latitude, longitude);

                                        // Imposta il centro della mappa sul GeoPoint appena creato
                                        mapView.getController().setCenter(location);

                                        Marker marker = new Marker(mapView);
                                        marker.setPosition(location);
                                        marker.setTitle(evento.getLuogo());
                                        //marker.setSnippet("Descrizione del luogo");
                                        mapView.getOverlays().add(marker);

                                        // Abilita il multitouch (zoom e pan)
                                        mapView.setMultiTouchControls(true);

                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            } else {
                                Log.d("Firestore", "Nessun documento trovato con ID: " + idEvento);
                            }
                        } else {
                            Log.e("Firestore", "Errore durante la ricerca del documento", task.getException());
                        }
                    }
                });

        // Gestione Preferiti / GIF
        heart = findViewById(R.id.heart);
        if(isEventFavourite)
            heart.setImageResource(R.drawable.heart_full);
        else
            heart.setImageResource(R.drawable.heart_empy);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Azioni da eseguire per aggiungere (o togliere) dai preferiti
                if(isEventFavourite) {
                    heart.setImageResource(R.drawable.heart_empy);
                    isEventFavourite = false;
                    //Inserire metodi per togliere dai preferiti
                }
                else{
                    heart.setImageResource(R.drawable.heart_full);
                    isEventFavourite = true;
                    //Inserire metodi per inserire tra i preferiti
                }
            }
        });

        //TASTO INDIETRO
        back = findViewById(R.id.arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    private void iscriviti(String idEvento, FirebaseAuth auth) {
        // Il resto del tuo codice per la gestione degli eventi
        // ...
    }
}
