package com.example.fansfun.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.example.fansfun.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;


public class PostRegistrationActivity extends AppCompatActivity {
    ImageView imageView;
    byte[] imageBytes;
    Button date;
    FloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.principal)));
        }

        imageView = findViewById(R.id.profile_pic);
        button = findViewById(R.id.floatingActionButton);
        date = findViewById(R.id.date);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog_date();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(PostRegistrationActivity.this)
                        .crop(1f,1f)
                        .compress(1024)
                        .maxResultSize(1080,1080)
                        .start();
            }
        });

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
            Uri uri = data.getData();

            try {
                imageBytes = getBytes(getContentResolver().openInputStream(uri)); //Immagine da salvare nel DB
                imageView.setImageURI(uri);
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
}