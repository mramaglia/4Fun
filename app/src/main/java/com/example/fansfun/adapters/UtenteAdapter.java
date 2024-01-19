package com.example.fansfun.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.fansfun.R;
import com.example.fansfun.entities.Utente;

import java.util.List;

public class UtenteAdapter extends ArrayAdapter<Utente> {

    public UtenteAdapter(Context context, List<Utente> utenti) {
        super(context, 0, utenti);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Utente utente = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_utente, parent, false);
        }

        TextView tvNome = convertView.findViewById(R.id.tvNome);
        TextView tvCognome = convertView.findViewById(R.id.tvCognome);
        TextView tvEmail = convertView.findViewById(R.id.tvEmail);
        TextView tvLuogo = convertView.findViewById(R.id.tvLuogo);
        TextView tvDataNascita = convertView.findViewById(R.id.tvDataNascita);

        tvNome.setText(utente.getNome());
        tvCognome.setText(utente.getCognome());
        tvEmail.setText(utente.getEmail());
        tvLuogo.setText(utente.getLuogo());
        tvDataNascita.setText((CharSequence) utente.getDataNascita()); //RIVEDERE QUESTO


        return convertView;
    }

}
