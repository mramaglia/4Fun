package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {
    ImageView imageView;
    FloatingActionButton button;
    EditText name, email, password;
    private FirebaseAuth auth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Sistema per poter inserire imamgine profilo ed avere anteprima
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.principal)));
        }
        imageView = findViewById(R.id.imageView2);
        button = findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(RegistrationActivity.this)
                        .crop(1f,1f)	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });




        auth= FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageView.setImageURI(uri);
    }

    public void login(View view){

        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

    }

    public void registrazione(View view){
    //ricorda di aggiungere gli altri parmetri all'utente (surname,data,cell)
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userName)){

            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();

            return;
        }

        if(TextUtils.isEmpty(userEmail)){

            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();

            return;
        }

        if(TextUtils.isEmpty(userPassword)){

            Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();

            return;
        }

        //RICORDARE: inserire foto profilo e data nell'utente
        //Per prelevare l'immagine si usa l'URI, da lì o si converte o si inserisce diretamente nel DB


        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Utente newUtente = new Utente(userName, null, userEmail, null, null);

                    FirebaseUser currentUser = auth.getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        db.collection("utenti").document(uid).set(newUtente);
                    }

                    Toast.makeText(RegistrationActivity.this, "Registrazione effettuata!", Toast.LENGTH_SHORT);
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                }else{
                    Toast.makeText(RegistrationActivity.this, "Registrazione fallita"+task.getException(), Toast.LENGTH_SHORT);
                }
            }
        });



    }
}