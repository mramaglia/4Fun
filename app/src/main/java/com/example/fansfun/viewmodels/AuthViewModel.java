package com.example.fansfun.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {
    private MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>();

    public AuthViewModel(){
        //Inizializza il viewModel con uno stato di non autenticazione
        isAuthenticated.setValue(false);
    }

    public MutableLiveData<Boolean> getIsAuthenticated(){
        return isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated){
        this.isAuthenticated.setValue(isAuthenticated);
    }
}
