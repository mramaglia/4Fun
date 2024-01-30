package com.example.fansfun.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fansfun.R;
import com.example.fansfun.entities.Comune;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InsertJSON extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore.setLoggingEnabled(true);
        setContentView(R.layout.activity_inser_json);
        new InsertJsonDataTask().execute();
    }

    private class InsertJsonDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Leggi il file JSON dalla cartella "assets"
                InputStream inputStream = getAssets().open("comuni.json");
                String json = readJsonFile(inputStream);

                // Parsa il JSON e inserisci nel database Firestore
                parseAndInsertData(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Puoi fare qualcosa dopo aver completato l'inserimento dei dati, se necessario
            Log.d("InsertJsonDataTask", "Inserimento dati completato");
        }
    }

    private String readJsonFile(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    private void parseAndInsertData(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        Executor executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonElement = jsonArray.getJSONObject(i);
            executor.execute(() -> {
                try {
                    // Esempio di come estrarre informazioni dal JSON
                    String nome = jsonElement.getString("nome");
                    String provincia = jsonElement.getJSONObject("provincia").getString("nome");
                    String regione = jsonElement.getJSONObject("regione").getString("nome");
                    String comuneId = jsonElement.getString("codice");
                    Log.d("InsertData", "Nome: " + nome + ", Provincia: " + provincia + ", Regione: " + regione + ", ComuneId: " + comuneId);
                    Comune comune = new Comune();
                    comune.setName(nome);
                    comune.setProvincia(provincia);
                    comune.setRegione(regione);

                    db.collection("comuni").document(comuneId).set(comune);
                    Log.d("InsertData", "INSERITO NEL DATABASE!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
