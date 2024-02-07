package com.example.fansfun.fragment;

import android.app.AlertDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fansfun.R;
import com.example.fansfun.activities.ViewEvent;
import com.example.fansfun.adapters.EventoAdapter;
import com.example.fansfun.entities.Evento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class WalletFragment extends Fragment {
    ListView listaWallet;
    String userName;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ottieni un riferimento al documento utente con l'ID desiderato
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("utenti").document(auth.getCurrentUser().getUid());

        // Esegui la query per ottenere il documento utente
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Il documento utente esiste, puoi ottenere il nome
                    userName = documentSnapshot.getString("nome")+ " " +documentSnapshot.getString("cognome");

                    // Ora puoi utilizzare il nome
                    Log.d("TAG", "Nome utente: " + userName);
                } else {
                    // Il documento utente non esiste
                    Log.d("TAG", "Documento utente non trovato");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Gestisci eventuali errori durante l'accesso al documento
                Log.e("TAG", "Errore durante l'accesso al documento utente", e);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        listaWallet=view.findViewById(R.id.listaWallet);

        listaWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Ottengo l'evento selezionato dalla posizione
                Evento eventoSelezionato = (Evento) parent.getItemAtPosition(position);

                // Creo un Intent per avviare la nuova Activity
                Intent intent = new Intent(getContext(), ViewEvent.class);

                intent.putExtra("evento", eventoSelezionato);

                // Avvia la nuova Activity
                startActivity(intent);
            }
        });

        listaWallet.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Evento eventoSelezionato = (Evento) parent.getItemAtPosition(position);

                return showCustomDialog(eventoSelezionato.getNome(), formatData(eventoSelezionato.getData()), eventoSelezionato.getId()+auth.getCurrentUser().getUid(), userName);
            }
        });

        return view;

    }

    public void updateWallet(List<Evento> listaEventi) {

        EventoAdapter adapter = new EventoAdapter(getContext(), R.layout.item_evento, listaEventi);

        // Impostare l'adattatore sulla ListView
        listaWallet.setAdapter(adapter);

    }

    private boolean showCustomDialog(String evName, String evDate, String evCode, String userName){

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.prenotation_dialog, null);

        // Ottenere un riferimento ai TextView nel layout del dialog
        TextView eventName = dialogLayout.findViewById(R.id.textView11);
        TextView nome = dialogLayout.findViewById(R.id.textView12);
        TextView data = dialogLayout.findViewById(R.id.textView13);
        ImageView id = dialogLayout.findViewById(R.id.textView14);

        // Modifica dei testi dei TextView
        eventName.setText("Evento: " + evName);
        nome.setText("Nome: " + userName);
        data.setText("Data: " + evDate);
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            // Crea una matrice di bit contenente il codice QR
            BitMatrix bitMatrix = barcodeEncoder.encode(evName+"\n"+userName+"\n"+evDate+"\n"+evCode, BarcodeFormat.QR_CODE, 512, 512);
            // Converti la matrice di bit in un'immagine Bitmap
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            // Visualizza l'immagine Bitmap nell'ImageView
            id.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Creazione del dialog utilizzando il layout inflato
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogLayout);

        // Aggiunta di pulsanti, titolo, etc. al dialog se necessario

        // Mostra il dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    private String formatData(Date data) {
        // Formattare la data come desiderato
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }

}