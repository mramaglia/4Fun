package com.example.fansfun.activities;

import static com.example.fansfun.activities.MainActivity.KEY_IS_AUTHENTICATED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fansfun.R;
import com.example.fansfun.viewmodels.AuthViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email, password;
    private FirebaseAuth auth;
    private AuthViewModel authViewModel;  // Aggiunto il ViewModel
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "my_shared_pref";  // Sostituisci con il nome desiderato
    ConstraintSet.Layout error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializza l'istanza del ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
    }

    public void login(View view) {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Inserisci un'email");

            return;
        }else if(TextUtils.isEmpty(userPassword)){
            password.setError("Inserisci una password");
            return;
        }else if (!isValidEmail(userEmail)) {
            email.setError("Indirizzo email non valido");
            return;
        }

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login effettuato!", Toast.LENGTH_SHORT).show();

                    // Salva lo stato di autenticazione nelle SharedPreferences
                    sharedPreferences.edit().putBoolean(KEY_IS_AUTHENTICATED, true).apply();
                    startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Errore durante il login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void registrazione(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }
}
