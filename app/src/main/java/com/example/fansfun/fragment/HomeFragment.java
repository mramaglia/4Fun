package com.example.fansfun.fragment;

import static android.content.Context.MODE_PRIVATE;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayoutStates;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fansfun.R;
import com.example.fansfun.activities.AddEvent;
import com.example.fansfun.activities.ListaEventi;
import com.example.fansfun.activities.MainActivity;
import com.example.fansfun.activities.PrincipalActivity;
import com.example.fansfun.activities.SearchEventList;

import com.example.fansfun.activities.ViewEvent;
import com.example.fansfun.adapters.HomeAdapter;
import com.example.fansfun.entities.Evento;
import com.example.fansfun.entities.Utente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private SearchView searchView;
    private TextView textView, location_2;
    private CardView cardConcerti, cardParty, cardFood, cardRaduni, cardNature, cardCultura, shuffle;

    private RecyclerView rv_vicinanze,rv_party, rv_newEvent;
    private HomeAdapter homeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textView=view.findViewById(R.id.location);
        textView.setText(userLocation());
        textView=view.findViewById(R.id.location_2);
        textView.setText(userLocation());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cardConcerti=view.findViewById(R.id.cardConcerti);
        cardParty=view.findViewById(R.id.cardParty);
        cardFood=view.findViewById(R.id.cardFood);
        cardRaduni= view.findViewById(R.id.cardRaduni);
        cardNature=view.findViewById(R.id.cardNature);
        cardCultura=view.findViewById(R.id.cardCultura);
        shuffle = view.findViewById(R.id.shuffle);

        rv_vicinanze = view.findViewById(R.id.rv_vicinanze);
        rv_party = view.findViewById(R.id.rv_party);
        rv_newEvent = view.findViewById(R.id.rv_newEvent);


        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), AddEvent.class);
                        startActivity(intent);
                    }
                });
            }
        });
        cardConcerti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchEventList.class);
                intent.putExtra("type", "category");
                intent.putExtra("category", "Concerti");
                startActivity(intent);
            }
        });
        cardParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchEventList.class);
                intent.putExtra("type", "category");
                intent.putExtra("category", "Party");
                startActivity(intent);
            }
        });
        cardFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchEventList.class);
                intent.putExtra("type", "category");
                intent.putExtra("category", "Food&Beverage");
                startActivity(intent);
            }
        });
        cardRaduni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchEventList.class);
                intent.putExtra("type", "category");
                intent.putExtra("category", "Raduni");
                startActivity(intent);
            }
        });
        cardNature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchEventList.class);
                intent.putExtra("type", "category");
                intent.putExtra("category", "Natura");
                startActivity(intent);
            }
        });
        cardCultura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchEventList.class);
                intent.putExtra("type", "category");
                intent.putExtra("category", "Cultura");
                startActivity(intent);
            }
        });

        //Button button = view.findViewById(R.id.button);

        //IMPLEMENTAZIONE RICERCA
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getContext(), SearchEventList.class);
                intent.putExtra("type", "text");
                intent.putExtra("search_query", query);
                startActivity(intent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });


        return view;
    }


    private String userLocation(){
        // Ottieni un riferimento alle SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("my_shared_pref", MODE_PRIVATE);

        String utenteJson = sharedPreferences.getString(MainActivity.KEY_USER, null);

        // Utilizza Gson per convertire la stringa JSON in un oggetto Utente
        Gson gson = new Gson();
        Utente utente = gson.fromJson(utenteJson, Utente.class);

        return utente.getLuogo();
    }

    public void updateNelleVicinanze(List<Evento> nelleVicinanze){

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rv_vicinanze.setLayoutManager(layoutManager);
            homeAdapter = new HomeAdapter(getContext(), nelleVicinanze);
            rv_vicinanze.setAdapter(homeAdapter);
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Gestisci il click sull'elemento della RecyclerView
                Evento selectedEvent = nelleVicinanze.get(position);
                // Esegui le azioni desiderate, ad esempio aprire una nuova Activity con i dettagli dell'evento
                openEventDetails(selectedEvent);
            }
        });

    }


    public void updateParty(List<Evento> eventList) {
        Log.e(ConstraintLayoutStates.TAG, "Errore durante il recupero dei documenti");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_party.setLayoutManager(layoutManager);
        homeAdapter = new HomeAdapter(getContext(), eventList);
        rv_party.setAdapter(homeAdapter);
        // Imposta il listener sull'adapter per gestire il click sull'elemento della RecyclerView
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Gestisci il click sull'elemento della RecyclerView
                Evento selectedEvent = eventList.get(position);
                // Esegui le azioni desiderate, ad esempio aprire una nuova Activity con i dettagli dell'evento
                openEventDetails(selectedEvent);
            }
        });
    }



    public void updateNewEvent(List<Evento> eventList) {
        Log.e(ConstraintLayoutStates.TAG, "Errore durante il recupero dei documenti");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_newEvent.setLayoutManager(layoutManager);
        homeAdapter = new HomeAdapter(getContext(), eventList);
        rv_newEvent.setAdapter(homeAdapter);
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Gestisci il click sull'elemento della RecyclerView
                Evento selectedEvent = eventList.get(position);
                // Esegui le azioni desiderate, ad esempio aprire una nuova Activity con i dettagli dell'evento
                openEventDetails(selectedEvent);
            }
        });
    }

    public void openEventDetails(Evento selectedEvent) {
        Intent intent = new Intent(getContext(), ViewEvent.class);
        intent.putExtra("evento", selectedEvent);
        startActivity(intent);
    }

}