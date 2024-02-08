package com.example.fansfun.activities;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fansfun.R;
import com.example.fansfun.entities.Utente;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {
    ImageView imageView;
    byte[] imageBytes;
    FloatingActionButton button;
    TextInputEditText name, surname, email, password;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth= FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

    }


    public void login(View view){

        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

    }

    public void registrazione(View view) {
        Intent intent = new Intent(RegistrationActivity.this, PostRegistrationActivity.class);

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Query per verificare se l'email esiste già nel database
            db.collection("utenti")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    // L'email è già presente nel database
                                    Toast.makeText(RegistrationActivity.this, "Questo indirizzo email è già in uso", Toast.LENGTH_SHORT).show();
                                } else {
                                    // L'email non esiste nel database, si può procedere con la registrazione
                                    intent.putExtra("email", userEmail);
                                    intent.putExtra("password", userPassword);
                                    startActivity(intent);
                                }
                            } else {
                                // Errore durante il recupero dei dati dal database
                                Exception e = task.getException();
                                Log.e(TAG, "Errore durante la verifica dell'email nel database", e);
                                Toast.makeText(RegistrationActivity.this, "Errore durante la registrazione: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}