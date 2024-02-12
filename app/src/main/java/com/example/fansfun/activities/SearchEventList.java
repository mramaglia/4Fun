package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fansfun.R;
import com.example.fansfun.adapters.EventoAdapter;
import com.example.fansfun.entities.Evento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchEventList extends AppCompatActivity {

    private ListView listView;
    private SearchView searchView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerCategoria;
    private AutoCompleteTextView filtroLuogo;
    private TextView textQuery;
    private String categoria, luogo="", searchQuery;
    private int indiceSelezionato = 0;
    private Button button;
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event_list);


        filtroLuogo=findViewById(R.id.filtro_luogo);
        spinnerCategoria = findViewById(R.id.filtro_categoria);
        String[] categorie = {"", "Concerti", "Party", "Food&Beverage", "Raduni", "Natura", "Cultura", "Altro"};

        button=findViewById(R.id.bottoneFiltri);
        listView=findViewById(R.id.searchList);
        searchView=findViewById(R.id.searchView2);
        autoCompleteTextView = findViewById(R.id.filtro_luogo);
        textQuery = findViewById(R.id.textView11);


        searchQuery= getIntent().getStringExtra("search_query");
        if(searchQuery != null) {
            textQuery.setText("Ricerca per:" + searchQuery);
            searchView.setQuery(searchQuery, false);
            spinnerCategoria.setSelection(indiceSelezionato);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Ottengo l'evento selezionato dalla posizione
                Evento eventoSelezionato = (Evento) parent.getItemAtPosition(position);

                // Creo un Intent per avviare la nuova Activity
                Intent intent = new Intent(SearchEventList.this, ViewEvent.class);

                // Aggiungo id agli extra
                intent.putExtra("evento", eventoSelezionato);

                // Avvia la nuova Activity
                startActivity(intent);
            }
        });


        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SearchEventList.this, SearchEventList.class);
                intent.putExtra("search_query", query);
                startActivity(intent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        updateListFilter(searchQuery, "", "", 0);

        //GESTIONE CATEGORIA
        ArrayAdapter<String> adapter_category = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorie);
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter_category);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = (String) parent.getItemAtPosition(position);
                indiceSelezionato = spinnerCategoria.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Quando non viene selezionato nulla
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                luogo=autoCompleteTextView.getText().toString();

                updateListFilter(searchQuery, luogo, categoria,indiceSelezionato);

            }
        });

        // Carica il file JSON
        String json = loadJSONFromAsset("comuni.json");

        // Ottieni la lista di opzioni dal JSON
        List<String> options = parseJSON(json);

        // Crea e imposta l'adattatore per l'autocompletamento
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        autoCompleteTextView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchEventList.this, PrincipalActivity.class);
        startActivity(intent);

    }

    private String loadJSONFromAsset(String filename) {
        String json;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private List<String> parseJSON(String json) {
        List<String> options = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonElement = jsonArray.getJSONObject(i);
                String nome = jsonElement.getString("nome");
                String provinciaNome = jsonElement.getJSONObject("provincia").getString("nome");
                String formattedData = nome + ", " + provinciaNome;
                options.add(formattedData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return options;
    }


    private void updateListFilter( String filtroTitolo, String filtroLuogo, String filtroCategoria, int i) {
        List<Evento> listaFiltrata = new ArrayList<>();

        CollectionReference eventiRef = db.collection("eventi");

        // Esegui la query per trovare eventi il cui campo "nome" contiene la sottostringa searchQuery
        eventiRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Elabora i documenti restituiti
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Evento evento = document.toObject(Evento.class);

                        // Applica i filtri
                        if (filtroTitolo.isEmpty() || evento.getNome().toLowerCase().contains(filtroTitolo.toLowerCase())) {
                            if (filtroLuogo.isEmpty() || evento.getLuogo().toLowerCase().contains(filtroLuogo.toLowerCase())) {
                                if (filtroCategoria.isEmpty() || evento.getCategoria().toLowerCase().equals(filtroCategoria.toLowerCase())) {
                                    listaFiltrata.add(evento);
                                }
                            }
                        }
                        // Aggiorna l'adapter del ListView con la lista filtrata
                        EventoAdapter adapter = new EventoAdapter(SearchEventList.this, R.layout.item_evento, listaFiltrata);
                        listView.setAdapter(adapter);
                    }




                } else {

                }
            }
        });
    }

}