package com.example.fansfun.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fansfun.R;
import com.example.fansfun.entities.Evento;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private Context context;
    private List<Evento> eventi;
    private OnItemClickListener listener; // Aggiunto il listener

    public HomeAdapter(Context context, List<Evento> eventi) {
        this.context = context;
        this.eventi = eventi;
    }

    // Metodo per impostare il listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Evento evento = eventi.get(position);

        String imageUrl = evento.getFoto();
        Glide.with(context).load(imageUrl).into(holder.imageView);

        holder.nomeTextView.setText(evento.getNome());
        holder.dataTextView.setText(formatData(evento.getData()));
    }

    @Override
    public int getItemCount() {
        return eventi.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nomeTextView, locationTextView, dataTextView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fotoImageView);
            nomeTextView = itemView.findViewById(R.id.nomeTextView);
            dataTextView = itemView.findViewById(R.id.dataTextView);

            // Imposta il listener sull'elemento della RecyclerView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        // Invoca il metodo onItemClick dell'interfaccia di callback
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    private String formatData(Date data) {
        if (data == null) {
            return ""; // o un'altra stringa di default, a seconda delle tue esigenze
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }

    // Interfaccia di callback per gestire il click sull'elemento
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

