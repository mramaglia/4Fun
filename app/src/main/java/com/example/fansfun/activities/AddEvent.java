package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class AddEvent extends AppCompatActivity {

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    ImageView imageView;
    byte[] imageBytes;
    TextInputEditText name, description;
    Button date, hour, addEvent;
    FloatingActionButton button;
    String eventName, eventDescription, imageUrl;
    Uri imageUri;
    int giorno, mese, anno, ora, minuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Inizializza Firebase Storage e Auth
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        date = findViewById(R.id.date);
        hour = findViewById(R.id.hour);
        addEvent = findViewById(R.id.addEvent);

        name = findViewById(R.id.EventName);
        description = findViewById(R.id.EventDescription);

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
                month_x += 1;

                //salvo giorno mese e anno nelle variabili globali per la creazione dell'evento alla pressione del button "addEvento"
                giorno=dayOfMonth_x;
                mese=month_x;
                anno=year_x;

                // Utilizza la data selezionata
                date.setText(String.valueOf(dayOfMonth_x) + "/" + String.valueOf(month_x) + "/" + String.valueOf(year_x));
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

    private void aggiungiEvento(){

        eventName = name.getText().toString();
        eventDescription = description.getText().toString();

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


        //prendo l'id dell'utente
        String userId = firebaseAuth.getCurrentUser().getUid();

        //operazione d aggiunta dell'immagine
        StorageReference imageRef = storageReference.child("eventImages/"+ userId);
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
                        db.collection("eventi")
                                .add(evento)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        // Documento aggiunto con successo
                                        Log.d("Firestore", "Documento aggiunto con ID: " + documentReference.getId());
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

