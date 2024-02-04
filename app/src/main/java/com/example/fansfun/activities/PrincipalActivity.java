package com.example.fansfun.activities;

import static androidx.databinding.DataBindingUtil.setContentView;

import static com.example.fansfun.activities.MainActivity.KEY_IS_AUTHENTICATED;
import static com.example.fansfun.activities.MainActivity.SHARED_PREF_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fansfun.R;
import com.example.fansfun.adapters.EventoAdapter;
import com.example.fansfun.entities.ListViewEvent;
import com.example.fansfun.entities.Utente;
import com.example.fansfun.fragment.FavouriteFragment;
import com.example.fansfun.fragment.HomeFragment;
import com.example.fansfun.fragment.ProfileFragment;
import com.example.fansfun.fragment.WalletFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomappbar.BottomAppBar;
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

public class PrincipalActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addEventButton;
    HomeFragment home = new HomeFragment();
    WalletFragment wallet = new WalletFragment();
    FavouriteFragment favourite = new FavouriteFragment();
    ProfileFragment profile = new ProfileFragment();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        addEventButton = findViewById(R.id.addEventButton);

        // Recupera l'utente dal database e aggiorna le SharedPreferencess
        retrieveUserFromDatabase();

        replaceFragment(new HomeFragment());


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(home);
            } else if (itemId == R.id.favourite) {
                //aggiungere metodo per query preferiti
                //retrieveFavouriteFromDatabase();
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
                startActivity(new Intent(PrincipalActivity.this, ListaEventi.class));
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

                // Aggiorna le SharedPreferences con i nuovi dati dell'utente
                saveUserToSharedPreferences(utente);

                // Aggiorna l'UI del fragment del profilo (se presente)
                if (profile != null && profile.isAdded()) {
                    profile.updateUserProfile(utente);
                }
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
                    List<ListViewEvent> listaEventi = new ArrayList<>();

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
                                        ListViewEvent evento = document.toObject(ListViewEvent.class);
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

}
