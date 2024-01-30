package com.example.fansfun.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fansfun.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button aggiungiEvento, profilo, eventi;
    private FirebaseFirestore firestore;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aggiungiEvento=findViewById(R.id.aggiungiEvento);
        profilo=findViewById(R.id.profilo);
        eventi=findViewById(R.id.eventi);

        aggiungiEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiEvento();
            }
        });
        profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profilo();
            }
        });
        eventi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventi();
            }
        });


    }

    private void aggiungiEvento(){

        Intent intent = new Intent(MainActivity.this, AddEvent.class);
        startActivity(intent);

    }

    private void eventi(){

        Toast.makeText(this, "E fattell tu", Toast.LENGTH_SHORT).show();

    }

    private void profilo(){

        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);

    }

}
