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
import com.example.fansfun.entities.Evento;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventoAdapter extends ArrayAdapter<Evento> {

    private Context context;
    private List<Evento> eventi;

    public EventoAdapter(@NonNull Context context, int resource, @NonNull List<Evento> eventi) {
        super(context, resource, eventi);
        this.context = context;
        this.eventi = eventi;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_evento, parent, false);
        }

        Evento eventoList = eventi.get(position);

        ImageView imageView = convertView.findViewById(R.id.fotoImageView);
        TextView nomeTextView = convertView.findViewById(R.id.nomeTextView);
        TextView descrizioneTextView = convertView.findViewById(R.id.descrizioneTextView);
        TextView dataTextView = convertView.findViewById(R.id.dataTextView);
        TextView luogoTextView = convertView.findViewById(R.id.luogoTextView);

        String imageUrl= eventoList.getFoto();
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        nomeTextView.setText(eventoList.getNome());
        descrizioneTextView.setText(eventoList.getDescrizione());
        dataTextView.setText(formatData(eventoList.getData()));
        luogoTextView.setText(eventoList.getLuogo());

        return convertView;


    }

    private String formatData(Date data) {
        // Formattare la data come desiderato
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }
}

