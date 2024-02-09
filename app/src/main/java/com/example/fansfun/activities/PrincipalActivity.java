package com.example.fansfun.activities;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import static androidx.databinding.DataBindingUtil.setContentView;

import static com.example.fansfun.activities.MainActivity.SHARED_PREF_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fansfun.R;
import com.example.fansfun.entities.Evento;
import com.example.fansfun.entities.Utente;
import com.example.fansfun.fragment.FavouriteFragment;
import com.example.fansfun.fragment.HomeFragment;
import com.example.fansfun.fragment.ProfileFragment;
import com.example.fansfun.fragment.WalletFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PrincipalActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addEventButton;
    HomeFragment home = new HomeFragment();
    WalletFragment wallet = new WalletFragment();
    FavouriteFragment favourite = new FavouriteFragment();
    ProfileFragment profile = new ProfileFragment();
    SharedPreferences sharedPreferences;

    private String luogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);



        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        addEventButton = findViewById(R.id.addEventButton);

        // Recupera l'utente dal database e aggiorna le SharedPreferencess
        retrieveUserFromDatabase();

        replaceFragment(home);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {

                replaceFragment(home);
            } else if (itemId == R.id.favourite) {
                retrieveFavouritesFromDatabase();
                replaceFragment(favourite);
            } else if (itemId == R.id.wallet) {
                retrieveWalletFromDatabase();
                replaceFragment(wallet);
            } else if (itemId == R.id.profile) {
                replaceFragment(profile);
            }

            return true;
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrincipalActivity.this, AddEvent.class));
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Nascondi tutti i fragment
        for (Fragment frag : fragmentManager.getFragments()) {
            fragmentTransaction.hide(frag);
        }

        // Mostra o aggiungi il nuovo fragment
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.frame_layout, fragment);
        }

        //fragmentTransaction.addToBackStack(null);   //non gestiamo il backstack
        fragmentTransaction.commit();

        // Seleziona l'elemento del BottomNavigationView solo se non è già selezionato
        int menuItemId = getMenuItemIdByFragment(fragment);
        if (bottomNavigationView.getSelectedItemId() != menuItemId) {
            bottomNavigationView.getMenu().findItem(menuItemId).setChecked(true);
        }
    }

    @Override
    public void onBackPressed(){
        if(bottomNavigationView.getSelectedItemId()==R.id.home) {
            super.onBackPressed();
            finish(); //exit app
        }
        else{
            bottomNavigationView.setSelectedItemId(R.id.home);
        }

    }

    private int getMenuItemIdByFragment(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            return R.id.home;
        } else if (fragment instanceof FavouriteFragment) {
            return R.id.favourite;
        } else if (fragment instanceof WalletFragment) {
            return R.id.wallet;
        } else if (fragment instanceof ProfileFragment) {
            return R.id.profile;
        }

        // Ritorna un valore predefinito o gestisci altri casi se necessario
        return R.id.home;
    }



    private void retrieveUserFromDatabase() {
        // Ottieni l'ID dell'utente corrente
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Riferimento al documento "utenti" nel Firestore
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("utenti").document(userId);

        // Recupera i dati dell'utente dal Firestore
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Il documento esiste, quindi l'utente è stato trovato
                Utente utente = documentSnapshot.toObject(Utente.class);

                luogo=utente.getLuogo();

                // Aggiorna le SharedPreferences con i nuovi dati dell'utente
                saveUserToSharedPreferences(utente);

                // Aggiorna l'UI del fragment del profilo (se presente)
                if (profile != null && profile.isAdded()) {
                    profile.updateUserProfile(utente);
                }
                //QUERY RIEMPIMENTO HOME
                retrieveEventsFromDatabase();
            } else {
                // Il documento non esiste, potrebbe essere necessario gestire questo caso a seconda delle esigenze
                Log.d("PrincipalActivity", "Il documento utente non esiste");
            }
        }).addOnFailureListener(e -> {
            // Gestisci eventuali errori durante il recupero dei dati dell'utente
            Log.e("PrincipalActivity", "Errore nel recupero dell'utente dal Firestore", e);
        });
    }

    private void retrieveWalletFromDatabase(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = db.collection("partecipaEvento").whereEqualTo("idUtente", userId);

        // Esecuzione della query
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Evento> listaEventi = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Converti il documento Firestore in un oggetto Evento

                        String idEvento = document.getString("idEvento");

                        DocumentReference docRef = FirebaseFirestore.getInstance().collection("eventi").document(idEvento);

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // Il documento esiste, puoi accedere ai suoi dati
                                        Evento evento = document.toObject(Evento.class);
                                        evento.setId(document.getId());
                                        listaEventi.add(evento);

                                        //ordino gli eventi per data
                                        Collections.sort(listaEventi, new DateComparator());

                                        wallet.updateWallet(listaEventi);
                                    } else {
                                        // Il documento non esiste
                                    }
                                } else {
                                    // Errore durante l'accesso al documento
                                    Log.e("Firestore", "Errore: " + task.getException());
                                }
                            }
                        });


                    }

                    // Ora listaEventi contiene tutti gli eventi con la categoria desiderata
                    // Puoi fare ciò che vuoi con la lista


                } else {
                    // Gestisci eventuali errori
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

    }



    private void saveUserToSharedPreferences(Utente utente) {
        if (utente != null) {
            sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            Gson gson = new Gson();
            String utenteJson = gson.toJson(utente);

            // Salva la stringa JSON nelle SharedPreferences
            sharedPreferences.edit().putString(MainActivity.KEY_USER, utenteJson).apply();
        }
    }

    private void retrieveFavouritesFromDatabase(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = db.collection("preferiti").whereEqualTo("idUtente", userId);

        // Esecuzione della query
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Evento> listaEventi = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Converti il documento Firestore in un oggetto Evento

                        String idEvento = document.getString("idEvento");

                        DocumentReference docRef = FirebaseFirestore.getInstance().collection("eventi").document(idEvento);

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // Il documento esiste, puoi accedere ai suoi dati
                                        Evento evento = document.toObject(Evento.class);
                                        evento.setId(document.getId());
                                        listaEventi.add(evento);

                                        //ordino gli eventi per data
                                        Collections.sort(listaEventi, new DateComparator());

                                        favourite.updatePreferiti(listaEventi);
                                    } else {
                                        // Il documento non esiste
                                    }
                                } else {
                                    // Errore durante l'accesso al documento
                                    Log.e("Firestore", "Errore: " + task.getException());
                                }
                            }
                        });


                    }

                    // Ora listaEventi contiene tutti gli eventi con la categoria desiderata
                    // Puoi fare ciò che vuoi con la lista


                } else {
                    // Gestisci eventuali errori
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void retrieveEventsFromDatabase() {
        Log.d(TAG, "retrieveEventsFromDatabase() chiamata"+ luogo.toString());
        String[] parts = luogo.split(", ");
        String provincia;
        if (parts.length > 1) {
            provincia = parts[1];
        } else {
            // Se il formato non è corretto o non contiene la provincia, restituisci una stringa vuota o null
            provincia = parts[1];
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Effettua una query per ottenere i documenti con un certo campo uguale
        db.collection("eventi")
                .whereEqualTo("luogo", luogo)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Evento> eventList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Il documento esiste, puoi accedere ai suoi dati
                                Evento evento = document.toObject(Evento.class);
                                evento.setId(document.getId());
                                eventList.add(evento);
                            }
                            // Aggiorna l'interfaccia utente con la lista degli eventi Party
                            home.updateNelleVicinanze(eventList);

                        } else {
                            // Gestisci eventuali errori durante il recupero dei documenti
                            Exception e = task.getException();
                            Log.e(TAG, "Errore durante il recupero dei documenti", e);
                        }
                    }
                });


        // Effettua un'altra query per gli eventi nelle vicinanze
        db.collection("eventi")
                .whereEqualTo("categoria", "Party")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Evento> eventoList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Il documento esiste, puoi accedere ai suoi dati
                                Evento evento = document.toObject(Evento.class);
                                evento.setId(document.getId());
                                eventoList.add(evento);
                            }
                            // Ordina gli eventi nelle vicinanze per data
                            Collections.sort(eventoList, new DateComparator());
                            // Aggiorna l'interfaccia utente con la lista degli eventi nelle vicinanze
                            home.updateParty(eventoList);
                        } else {
                            // Gestisci eventuali errori durante il recupero dei documenti
                            Exception e = task.getException();
                            Log.e(TAG, "Errore durante il recupero dei documenti PARTY", e);
                        }
                    }
                });
        db.collection("eventi")
                .whereEqualTo("categoria", "Concerti")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Evento> eventoList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Il documento esiste, puoi accedere ai suoi dati
                                Evento evento = document.toObject(Evento.class);
                                evento.setId(document.getId());
                                eventoList.add(evento);
                            }
                            // Ordina gli eventi nelle vicinanze per data
                            Collections.sort(eventoList, new DateComparator());
                            // Aggiorna l'interfaccia utente con la lista degli eventi nelle vicinanze
                            home.updateNewEvent(eventoList);
                        } else {
                            // Gestisci eventuali errori durante il recupero dei documenti
                            Exception e = task.getException();
                            Log.e(TAG, "Errore durante il recupero dei documenti PARTY", e);
                        }
                    }
                });
    }
    
}
