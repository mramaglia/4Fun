package com.example.fansfun.adapters;
import android.content.Context;
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

        ListViewEvent evento = eventi.get(position);

        ImageView imageView = convertView.findViewById(R.id.fotoImageView);
        TextView nomeTextView = convertView.findViewById(R.id.nomeTextView);
        TextView dataTextView = convertView.findViewById(R.id.dataTextView);
        TextView luogoTextView = convertView.findViewById(R.id.luogoTextView);
        TextView numPartecipanti = convertView.findViewById(R.id.numPartecipantiTextView);
        TextView organizzatore = convertView.findViewById(R.id.organizzatoreTextView);

        /*String imageUrl=evento.getFoto();
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);*/
        nomeTextView.setText(evento.getNome());
        dataTextView.setText(formatData(evento.getData()));
        luogoTextView.setText(evento.getLuogo());
        numPartecipanti.setText(String.valueOf(evento.getNumPartecipanti()));
        organizzatore.setText(evento.getId());

        return convertView;
    }

    private String formatData(Date data) {
        // Formattare la data come desiderato
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(data);
    }
}

