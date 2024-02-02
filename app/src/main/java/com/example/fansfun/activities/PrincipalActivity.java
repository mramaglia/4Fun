package com.example.fansfun.activities;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fansfun.R;
import com.example.fansfun.fragment.FavouriteFragment;
import com.example.fansfun.fragment.HomeFragment;
import com.example.fansfun.fragment.ProfileFragment;
import com.example.fansfun.fragment.WalletFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PrincipalActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        addEventButton=findViewById(R.id.addEventButton);

        replaceFragment(new HomeFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.favourite) {
                replaceFragment(new FavouriteFragment());
            } else if (itemId == R.id.wallet) {
                replaceFragment(new WalletFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrincipalActivity.this, AddEvent.class));
            }
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
