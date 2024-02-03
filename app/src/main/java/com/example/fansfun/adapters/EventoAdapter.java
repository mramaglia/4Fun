package com.example.fansfun.adapters;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.fansfun.R;
import com.example.fansfun.activities.ListaEventi;
import com.example.fansfun.activities.ViewEvent;
import com.example.fansfun.entities.Evento;
import com.example.fansfun.entities.ListViewEvent;
import com.example.fansfun.entities.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventoAdapter extends ArrayAdapter<ListViewEvent> {

    private Context context;
    private List<ListViewEvent> eventi;

    public EventoAdapter(@NonNull Context context, int resource, @NonNull List<ListViewEvent> eventi) {
        super(context, resource, eventi);
        this.context = context;
        this.eventi = eventi;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_evento, parent, false);
        }

        ListViewEvent eventoList = eventi.get(position);

        ImageView imageView = convertView.findViewById(R.id.fotoImageView);
        TextView nomeTextView = convertView.findViewById(R.id.nomeTextView);
        TextView dataTextView = convertView.findViewById(R.id.dataTextView);
        TextView luogoTextView = convertView.findViewById(R.id.luogoTextView);

        /*String imageUrl=evento.getFoto();
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);*/
        nomeTextView.setText(eventoList.getNome());
        dataTextView.setText(formatData(eventoList.getData()));
        luogoTextView.setText(eventoList.getLuogo());

        return convertView;


    }

    private String formatData(Date data) {
        // Formattare la data come desiderato
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(data);
    }
}

