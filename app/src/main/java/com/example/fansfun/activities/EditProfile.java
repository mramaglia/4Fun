package com.example.fansfun.activities;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fansfun.R;
import com.example.fansfun.entities.Utente;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private TextInputEditText nome, cognome;
    private FloatingActionButton buttonImg;
    private AutoCompleteTextView luogo;
    private ImageView profileImg;
    private ImageView update;
    private SharedPreferences sharedPreferences;
    private String newNome, newCognome, newLuogo;
    private byte[] imageBytes;
    private Uri imageUri;
    private Utente utente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ottieni un riferimento alle SharedPreferences
        sharedPreferences = getSharedPreferences("my_shared_pref", MODE_PRIVATE);

        // Recupera la stringa JSON dell'Utente
        String utenteJson = sharedPreferences.getString(MainActivity.KEY_USER, null);

        // Utilizza Gson per convertire la stringa JSON in un oggetto Utente
        Gson gson = new Gson();
        utente = gson.fromJson(utenteJson, Utente.class);

        String json = loadJSONFromAsset("comuni.json");
        // Ottieni la lista di opzioni dal JSON
        List<String> options = parseJSON(json);

        nome=findViewById(R.id.nameEdit);
        cognome=findViewById(R.id.surnameEdit);
        luogo=findViewById(R.id.autoCompleteTextViewEdit);
        update=findViewById(R.id.updateEdit);
        profileImg=findViewById(R.id.profile_pic);
        buttonImg=findViewById(R.id.floatingActionButton);

        nome.setText(utente.getNome());
        cognome.setText(utente.getCognome());
        luogo.setText(utente.getLuogo());
        String imageUrl = utente.getFoto();
        Glide.with(this)
                .load(imageUrl)
                .transform()
                .into(profileImg);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        luogo.setAdapter(adapter);

        buttonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfile.this)
                        .crop(1f,1f)
                        .compress(1024)
                        .maxResultSize(1080,1080)
                        .start();
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                imageBytes = getBytes(getContentResolver().openInputStream(imageUri)); //Immagine da salvare nel DB
                profileImg.setImageURI(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private byte[] getBytes(InputStream inputStream) throws IOException {  //serve per prelevare i byte
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void updateProfile(String idUtente){

        newNome=nome.getText().toString();
        newCognome=cognome.getText().toString();
        newLuogo=luogo.getText().toString();

        // Ottieni un riferimento al documento che desideri aggiornare
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("utenti").document(idUtente);

        // Crea un map contenente i campi da aggiornare e i relativi nuovi valori
        Map<String, Object> updates = new HashMap<>();
        updates.put("nome", newNome);
        updates.put("cognome", newCognome);
        updates.put("luogo", newLuogo);

        if (imageUri == null)
            updateSenzaImmagine(updates, idUtente);

        else {

            //operazione d aggiunta dell'immagine
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("userImages/" + idUtente);
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //gestisci l'errore
                    Toast.makeText(EditProfile.this, "Errore nell'aggiunta dell'immagine profilo", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Il caricamento è stato completato con successo, ora ottieni l'URL

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            updates.put("foto", downloadUrl.toString());

                            // Esegui l'aggiornamento del documento
                            docRef.update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(EditProfile.this, PrincipalActivity.class));
                                            Log.d(TAG, "Documento aggiornato con successo!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Errore durante l'aggiornamento del documento", e);
                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Gestisci eventuali errori
                        }
                    });

                }
            });


            // Esegui l'aggiornamento del documento
            docRef.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(EditProfile.this, PrincipalActivity.class));
                            Log.d(TAG, "Documento aggiornato con successo!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Errore durante l'aggiornamento del documento", e);
                        }
                    });
        }

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


    private void updateSenzaImmagine(Map<String, Object> updates, String idUtente){

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("utenti").document(idUtente);

        // Esegui l'aggiornamento del documento
        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(EditProfile.this, PrincipalActivity.class));
                        Log.d(TAG, "Documento aggiornato con successo!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Errore durante l'aggiornamento del documento", e);
                    }
                });
    }


}