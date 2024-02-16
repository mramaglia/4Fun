package com.example.fansfun.activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.fansfun.R;
import com.example.fansfun.entities.Evento;
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
public class AddEvent extends AppCompatActivity {
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    ImageView imageView, addEvent, arrow;
    byte[] imageBytes;
    TextInputEditText name, description, address;
    AutoCompleteTextView location;
    Button date, hour;
    FloatingActionButton button;
    String eventName, eventDescription, luogo, categoria, indirizzo;
    Uri imageUri;
    Spinner categoryView;
    int giorno, mese, anno, ora, minuto;
    Spinner category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        /*
            --------------------------NOTE----------------------------
            implementare categorie
            implementare gli errori su ogni textEditText con setError
            Ricordarsi di implementare metodi per numero massimo persone in un evento (se n = 0 llora "evento pieno")
         */
        // Inizializza Firebase Storage e Auth
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();



        arrow = findViewById(R.id.arrow2);


        date = findViewById(R.id.date);
        hour = findViewById(R.id.hour);
        addEvent=findViewById(R.id.imageView6);
        address=findViewById(R.id.EventAddress);
        location=findViewById(R.id.autoCompleteTextView);
        name = findViewById(R.id.EventName);
        description = findViewById(R.id.EventDescription);

        categoryView = findViewById(R.id.cateogry);   //Michele so che è sbagliato, se cambio, però devo cambiare tutto il layout <3 :)
        String[] categorie = {"Concerti", "Party", "Food&Beverage", "Raduni", "Natura", "Cultura", "Altro"};
        // Carica il file JSON
        String json = loadJSONFromAsset("comuni.json");
        // Ottieni la lista di opzioni dal JSON
        List<String> options = parseJSON(json);
        // Inizializza l'AutoCompleteTextView
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        // Crea e imposta l'adattatore per l'autocompletamento
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        autoCompleteTextView.setAdapter(adapter);
        //sistema dialog per data, metodi aggiuntivi in basso
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog_date();
            }
        });
        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog_hour();
            }
        });
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiEvento();
            }
        });
        //Sistema per poter inserire immagine profilo ed avere anteprima
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.principal)));
        }
        imageView = findViewById(R.id.EventPhoto);
        button = findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(AddEvent.this)
                        .crop(1f, 1f)                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        //GESTIONE CATEGORIA
        ArrayAdapter<String> adapter_category = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorie);
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryView.setAdapter(adapter_category);
        categoryView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Quando non viene selezionato nulla
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
    //Dialog per selezione data e ora
    private void openDialog_date() {
        // Ottieni la data corrente
        LocalDate currentDate = LocalDate.now();
        // Crea un DatePickerDialog con limite minimo alla data corrente
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year_x, int month_x, int dayOfMonth_x) {
                // Aggiungi 1 al mese perché il DatePickerDialog rappresenta i mesi da 0 a 11
                    //month_x += 1;
                //salvo giorno mese e anno nelle variabili globali per la creazione dell'evento alla pressione del button "addEvento"
                giorno=dayOfMonth_x;
                mese=month_x;
                anno=year_x;
                // Utilizza la data selezionata
                date.setText(String.valueOf(dayOfMonth_x) + "/" + String.valueOf(month_x+1) + "/" + String.valueOf(year_x));
            }
        }, currentDate.getYear(), currentDate.getMonthValue() -1, currentDate.getDayOfMonth());
        dialog.getDatePicker().setMinDate(currentDate.toEpochDay() * 24 * 60 * 60 * 1000);
        dialog.show();
    }
    private void openDialog_hour() {
        Calendar currentTime = Calendar.getInstance();
        // Aggiungi un'ora all'orario corrente
        currentTime.add(Calendar.HOUR_OF_DAY, 1);
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //salvo ora e minuti nelle variabili globali per la creazione dell'evento alla pressione del button "addEvento"
                ora=hourOfDay;
                minuto=minute;
                hour.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
            }
        },currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
        dialog.show();
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
    private void aggiungiEvento(){

        Toast.makeText(AddEvent.this, "Creazione dell'evento...", Toast.LENGTH_SHORT).show();

        eventName = name.getText().toString();
        eventDescription = description.getText().toString();
        indirizzo=address.getText().toString();

        //creo l'evento
        Evento evento = new Evento();
        //setto il nome
        evento.setNome(eventName);
        //setto la descrizione
        evento.setDescrizione(eventDescription);
        //aggiungi la data
        Calendar calendar = Calendar.getInstance();
        calendar.set(anno, mese, giorno, ora, minuto);
        Date specificDate = calendar.getTime();
        evento.setData(specificDate);
        //aggiungi luogo
        luogo=location.getText().toString();
        evento.setLuogo(luogo);

        //aggiungi indirizzo
        evento.setIndirizzo(indirizzo);

        //aggiungi categoria
        evento.setCategoria(categoria);

        //prendo l'id dell'utente
        String userId = firebaseAuth.getCurrentUser().getUid();
        //aggiungo id utente
        evento.setOrganizzatore(userId);

        db.collection("eventi")
                .add(evento)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Documento aggiunto con successo
                        Log.d("Firestore", "Documento aggiunto con ID: " + documentReference.getId());

                        documentReference.update("id", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // ID aggiunto con successo al documento
                                        Log.d("Firestore", "ID aggiunto con successo al documento " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Gestisci eventuali errori
                                    }
                                });

                        //operazione d aggiunta dell'immagine
                        StorageReference imageRef = storageReference.child("eventImages/"+ documentReference.getId());
                        UploadTask uploadTask = imageRef.putFile(imageUri);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //gestisci l'errore
                                Toast.makeText(AddEvent.this, "Errore nell'aggiunta dell'immagine profilo", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Il caricamento è stato completato con successo, ora ottieni l'URL
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUrl) {
                                        evento.setFoto(downloadUrl.toString());
                                        // Aggiungi un nuovo documento con un ID generato automaticamente

                                        documentReference.update("foto", downloadUrl.toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // ID aggiunto con successo al documento
                                                        Log.e("Storage", "Foto aggiunta con successo al documento " + "eventImages/"+ documentReference.getId());
                                                        Intent intent = new Intent(AddEvent.this, PrincipalActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Gestisci eventuali errori
                                                        Intent intent = new Intent(AddEvent.this, PrincipalActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gestisci l'errore
                        Log.w("Firestore", "Errore nell'aggiungere il documento", e);
                    }
                });

        //Intent intent = new Intent(AddEvent.this, PrincipalActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);

    }

}