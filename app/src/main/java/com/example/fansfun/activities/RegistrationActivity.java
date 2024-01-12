package com.example.fansfun.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fansfun.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, email, password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth= FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }

    public void login(View view){

        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

    }

    public void registrazione(View view){

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

        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this, "Registrazione effettuata!", Toast.LENGTH_SHORT);
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                }else{
                    Toast.makeText(RegistrationActivity.this, "Registrazione fallita"+task.getException(), Toast.LENGTH_SHORT);
                }
            }
        });



    }
}