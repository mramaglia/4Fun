package com.example.fansfun.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.fansfun.R;
import com.example.fansfun.viewmodels.AuthViewModel;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_USER = "user_data";
    static final String KEY_IS_AUTHENTICATED = "is_authenticated";
    static final String SHARED_PREF_NAME = "my_shared_pref";

    private AuthViewModel viewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inizializza l'istanza di SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        // Inizializzazione ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Delay di 5 secondi prima di passare all'altra attività
        new Handler().postDelayed(() -> {
            // Ottenere stato dell'istanza del ViewModel
            viewModel.getIsAuthenticated().observe(this, isAuthenticated -> {
                Log.d("MainActivity", "Valore di KEY_IS_AUTHENTICATED: " + isUserAuthenticated());

                if (isUserAuthenticated()) {
                    // Utente autenticato, passa alla schermata principale
                    startActivity(new Intent(MainActivity.this, PrincipalActivity.class));
                    finish();
                } else {
                    // Utente non autenticato, passa alla schermata di login
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }, 1500); // Ritardo di 5 secondi
    }

    private boolean isUserAuthenticated(){
        return sharedPreferences.getBoolean(KEY_IS_AUTHENTICATED, false);
    }
}
