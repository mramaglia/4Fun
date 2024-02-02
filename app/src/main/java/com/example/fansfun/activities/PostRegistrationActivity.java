package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fansfun.entities.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fansfun.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PostRegistrationActivity extends AppCompatActivity {

    ImageView imageView, imageView3;
    byte[] imageBytes;
    Button date;
    FloatingActionButton button;
    TextInputEditText name, surname;
    AutoCompleteTextView location;
    String nome, cognome, luogo, userEmail, userPassword;
    int giorno, mese, anno;
    Uri imageUri;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    boolean isData=false, isFoto=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.principal)));
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        auth= FirebaseAuth.getInstance();
        imageView = findViewById(R.id.profile_pic);
        button = findViewById(R.id.floatingActionButton);
        date = findViewById(R.id.date);
        name=findViewById(R.id.name);
        surname=findViewById(R.id.surname);
        location=findViewById(R.id.autoCompleteTextView);
        imageView3=findViewById(R.id.imageView3);

        Bundle extras = getIntent().getExtras();
        userEmail = extras.getString("email");
        userPassword = extras.getString("password");


            // Carica il file JSON
            String json = loadJSONFromAsset("comuni.json");

            // Ottieni la lista di opzioni dal JSON
            List<String> options = parseJSON(json);

            // Inizializza l'AutoCompleteTextView
            AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

            // Crea e imposta l'adattatore per l'autocompletamento
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
            autoCompleteTextView.setAdapter(adapter);


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isData=true;
                openDialog_date();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFoto=true;
                ImagePicker.with(PostRegistrationActivity.this)
                        .crop(1f,1f)
                        .compress(1024)
                        .maxResultSize(1080,1080)
                        .start();
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrazione();
            }
        });



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

    private void openDialog_date() {
        // Ottieni la data corrente
        LocalDate currentDate = LocalDate.now();
        // Crea un DatePickerDialog con limite minimo alla data corrente
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year_x, int month_x, int dayOfMonth_x) {
                // Aggiungi 1 al mese perché il DatePickerDialog rappresenta i mesi da 0 a 11
                month_x += 1;

                anno=year_x;
                mese=month_x;
                giorno=dayOfMonth_x;

                // Utilizza la data selezionata
                date.setText(String.valueOf(dayOfMonth_x) + "/" + String.valueOf(month_x) + "/" + String.valueOf(year_x));
            }
        }, currentDate.getYear(), currentDate.getMonthValue() -1, currentDate.getDayOfMonth());
        dialog.show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                imageBytes = getBytes(getContentResolver().openInputStream(imageUri)); //Immagine da salvare nel DB
                imageView.setImageURI(imageUri);
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

    private void registrazione(){


        nome=name.getText().toString();
        cognome=surname.getText().toString();
        luogo=location.getText().toString();

        if(TextUtils.isEmpty(nome)){

            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();

            return;
        }

        if(TextUtils.isEmpty(cognome)){

            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();

            return;
        }

        if(TextUtils.isEmpty(luogo)){

            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();

            return;
        }

        if(!isData){

            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();

            return;
        }

        if(!isFoto){

            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();

            return;
        }


        //aggiungi la data
        Calendar calendar = Calendar.getInstance();
        calendar.set(anno, mese, giorno);
        Date specificDate = calendar.getTime();

        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(PostRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PostRegistrationActivity.this, "Registrazione andata a buon fine!\n Attendi che la tua regitrazione venga elaborata", Toast.LENGTH_SHORT).show();

                    Utente newUtente = new Utente();
                    newUtente.setNome(nome);
                    newUtente.setCognome(cognome);
                    newUtente.setLuogo(luogo);
                    newUtente.setEmail(userEmail);
                    newUtente.setDataNascita(specificDate);

                    //prendo l'id dell'utente
                    FirebaseUser currentUser = auth.getCurrentUser();
                    String userId = currentUser.getUid();

                    //operazione d aggiunta dell'immagine
                    StorageReference imageRef = storageReference.child("userImages/"+ userId);
                    UploadTask uploadTask = imageRef.putFile(imageUri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //gestisci l'errore
                            Toast.makeText(PostRegistrationActivity.this, "Errore nell'aggiunta dell'immagine profilo", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Il caricamento è stato completato con successo, ora ottieni l'URL

                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    newUtente.setFoto(downloadUrl.toString());

                                    //aggiungo documento con id=userId
                                    db.collection("utenti").document(userId).set(newUtente) //Inserimento nel DB
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Documento aggiunto o aggiornato con successo
                                                    Log.d("Firestore", "Documento aggiunto o aggiornato con successo");
                                                    startActivity(new Intent(PostRegistrationActivity.this, PrincipalActivity.class));

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Gestisci l'errore
                                                    Log.w("Firestore", "Errore nell'aggiungere il documento", e);
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
                }
            }
        });

    }

}