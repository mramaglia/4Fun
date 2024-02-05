package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fansfun.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ImageView profileImage, back;
    private TextView textNome, textLuogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth=FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        profileImage=findViewById(R.id.imageView);
        textNome=findViewById(R.id.textView6);
        textLuogo=findViewById(R.id.textView7);

        String userId = auth.getCurrentUser().getUid(); // Sostituisci con l'ID dell'utente
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("utenti").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nome = document.getString("nome");
                        textNome.setText(nome);
                        String luogo = document.getString("luogo");
                        textLuogo.setText(luogo);
                        String imageUrl=document.getString("foto");
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .into(profileImage);

                        // Puoi utilizzare i dati qui
                    } else {
                        Log.d("Firestore", "Nessun documento trovato con questo ID");
                    }
                } else {
                    Log.d("Firestore", "Errore nel recupero del documento", task.getException());
                }
            }
        });

        //back pressed
        back = findViewById(R.id.arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}