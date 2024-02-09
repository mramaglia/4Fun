package com.example.fansfun.activities;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private ImageView imageView, iscriviti, heart, back, delete, imageOrganizzatore;
    private TextView nome, descrizione, luogo, data, ora;
    private ConstraintLayout tastoIscrizione;
    private Drawable drawable;
    private boolean isExpired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));

        Evento evento = (Evento) getIntent().getSerializableExtra("evento");
        String idEvento=evento.getId();
        auth = FirebaseAuth.getInstance();

        delete = findViewById(R.id.delete);

        if(isAuthor(evento.getOrganizzatore()))
            delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminaEvento(evento.getId());
            }
        });


        MapView mapView = findViewById(R.id.mapView);
        imageView = findViewById(R.id.imageView);
        nome = findViewById(R.id.textView9);
        descrizione = findViewById(R.id.textView10);
        luogo = findViewById(R.id.textView14);
        data = findViewById(R.id.eventDate);
        ora = findViewById(R.id.eventHour);
        iscriviti = findViewById(R.id.imageView6);
        tastoIscrizione=findViewById(R.id.layoutTastoIscrizione);
        imageOrganizzatore=findViewById(R.id.imageProfileOrganizzatore);


        // Ottieni il drawable dalla risorsa
        drawable = ContextCompat.getDrawable(ViewEvent.this, R.drawable.uncheck);

        // Modifica il colore del drawable
        DrawableCompat.setTint(drawable, Color.WHITE);

        db = FirebaseFirestore.getInstance();

        Geocoder geocoder = new Geocoder(this);

        existIscritto(idEvento, auth);

        String imageUrl = evento.getFoto();
        Glide.with(ViewEvent.this)
                .load(imageUrl)
                .into(imageView);

        nome.setText(evento.getNome());
        descrizione.setText(evento.getDescrizione());
        luogo.setText(evento.getIndirizzo() + ", " + evento.getLuogo());
        data.setText(formatData(evento.getData()));
        ora.setText(formatOra(evento.getData()));

        DocumentReference docRef = db.collection("utenti").document(evento.getOrganizzatore());

        // Ottieni il campo desiderato del documento
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Ottieni il campo desiderato
                        String userImgUrl = document.getString("foto");
                        Glide.with(ViewEvent.this)
                                .load(userImgUrl)
                                .into(imageOrganizzatore);
                    } else {
                        // Il documento non esiste
                    }
                } else {
                    // Errore durante l'accesso al documento
                    Log.d(TAG, "Errore: ", task.getException());
                }
            }
        });

        // Imposta il livello di zoom iniziale
        mapView.getController().setZoom(15.0);

        String locationName = evento.getIndirizzo()+ ", " + evento.getLuogo(); // Sostituisci con il tuo indirizzo o comune
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

        imageOrganizzatore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEvent.this, ProfileActivity.class);
                intent.putExtra("user_id", evento.getOrganizzatore());
                startActivity(intent);
            }
        });

        iscriviti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iscriviti(idEvento, auth);
            }
        });

        // Gestione Preferiti / GIF
        heart = findViewById(R.id.heart);

        existPreferiti(idEvento, auth);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferiti(idEvento, auth);
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
        Map<String, Object> datiDocumento = new HashMap<>();
        //prendo l'id dell'utente
        String idUtente=auth.getCurrentUser().getUid();
        // Aggiungi i campi desiderati al documento
        datiDocumento.put("idUtente", idUtente);
        datiDocumento.put("idEvento", idEvento);
        String idDocumento=idUtente+idEvento;
        DocumentReference documentReference = db.collection("partecipaEvento").document(idDocumento);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Il documento esiste
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firestore", "Documento eliminato con successo");
                                DocumentReference documentRef = db.collection("eventi").document(idEvento);
                                // Esegui l'aggiornamento del campo
                                tastoIscrizione.setBackgroundColor(Color.parseColor("#2DCA7E"));
                                iscriviti.setImageResource(R.drawable.check);
                                Toast.makeText(ViewEvent.this, "Hai annullato la tua partecipazione all'evento", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", "Errore durante l'eliminazione del documento: " + e.getMessage());
                            }
                        });
                    } else {
                        // Il documento non esiste
                        documentReference.set(datiDocumento)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Firestore", "Documento aggiunto con successo");
                                        tastoIscrizione.setBackgroundColor(Color.RED);
                                        iscriviti.setImageDrawable(drawable);
                                        Toast.makeText(ViewEvent.this, "Ora partecipi all'evento!", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Firestore", "Errore durante l'aggiunta del documento: " + e.getMessage());
                                    }
                                });
                    }
                } else {
                    // Gestisci eventuali errori durante la query
                    Log.e("Firestore", "Errore durante la query: " + task.getException());
                }
            }
        });
    }

    private void preferiti(String idEvento, FirebaseAuth auth) {
        Map<String, Object> datiDocumento = new HashMap<>();
        //prendo l'id dell'utente
        String idUtente=auth.getCurrentUser().getUid();
        // Aggiungi i campi desiderati al documento
        datiDocumento.put("idUtente", idUtente);
        datiDocumento.put("idEvento", idEvento);
        String idDocumento=idUtente+idEvento;
        DocumentReference documentReference = db.collection("preferiti").document(idDocumento);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Il documento esiste
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //documento eliminato: rimosso dai preferiti
                                heart.setImageResource(R.drawable.heart_empy);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", "Errore durante l'eliminazione del documento: " + e.getMessage());
                            }
                        });
                    } else {
                        // Il documento non esiste
                        documentReference.set(datiDocumento)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //documento aggiunto: evento aggiunto ai preferiti
                                        heart.setImageResource(R.drawable.heart_full);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Firestore", "Errore durante l'aggiunta del documento: " + e.getMessage());
                                    }
                                });
                    }
                } else {
                    // Gestisci eventuali errori durante la query
                    Log.e("Firestore", "Errore durante la query: " + task.getException());
                }
            }
        });
    }

    private void existPreferiti(String idEvento, FirebaseAuth auth){

        String idDocumento = auth.getCurrentUser().getUid()+idEvento;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("preferiti").document(idDocumento);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Il documento esiste
                            heart.setImageResource(R.drawable.heart_full);

                        } else {
                            // Il documento non esiste
                            heart.setImageResource(R.drawable.heart_empy);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gestisci eventuali errori durante il recupero del documento
                    }
                });


    }

    private void existIscritto(String idEvento, FirebaseAuth auth){

        String idDocumento = auth.getCurrentUser().getUid()+idEvento;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("partecipaEvento").document(idDocumento);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Il documento esiste
                            tastoIscrizione.setBackgroundColor(Color.RED);
                            iscriviti.setImageDrawable(drawable);

                        } else {
                            // Il documento non esiste
                            tastoIscrizione.setBackgroundColor(Color.parseColor("#2DCA7E"));
                            iscriviti.setImageResource(R.drawable.check);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gestisci eventuali errori durante il recupero del documento
                    }
                });


    }

    public boolean isAuthor(String idOrganizzatore){

        if(idOrganizzatore.equals(auth.getCurrentUser().getUid())){
            return true;
        }
        else {
            return false;
        }
    }

    private void eliminaEvento(String idEvento){
        eliminaDaPartecipa(idEvento);
        eliminaDaPreferiti(idEvento);
        eliminaDaEvento(idEvento);
    }

    private void eliminaDaPartecipa(String idEvento){

        // Effettua una query per trovare il documento con un campo uguale a un certo valore
        db.collection("partecipaEvento")
                .whereEqualTo("idEvento", idEvento)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Per ogni documento trovato, eliminare il documento
                                db.collection("partecipaEvento").document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Documento eliminato con successo da partecipaevento");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Errore durante l'eliminazione del documento", e);
                                            }
                                        });
                            }
                        } else {
                            Log.e(TAG, "Errore durante il recupero dei documenti", task.getException());
                        }
                    }
                });

    }

    private void eliminaDaPreferiti(String idEvento){

        db.collection("preferiti")
                .whereEqualTo("idEvento", idEvento)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Per ogni documento trovato, eliminare il documento
                                db.collection("preferiti").document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Documento eliminato con successo");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Errore durante l'eliminazione del documento", e);
                                            }
                                        });
                            }
                        } else {
                            Log.e(TAG, "Errore durante il recupero dei documenti", task.getException());
                        }
                    }
                });
    }

    private void eliminaDaEvento(String idEvento){
        DocumentReference documentDaEliminare = db.collection("eventi").document(idEvento);

        // Elimina il documento
        documentDaEliminare.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Documento eliminato con successo
                        Intent intent = new Intent(ViewEvent.this, PrincipalActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gestisci eventuali errori durante l'eliminazione del documento
                    }
                });
    }

}
