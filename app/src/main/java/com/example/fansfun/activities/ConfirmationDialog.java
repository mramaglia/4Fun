package com.example.fansfun.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.fansfun.R;

public class ConfirmationDialog {

    public interface ConfirmationListener {
        void onConfirm();
        void onCancel();
    }

    public static void show(Context context, String message, final ConfirmationListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.confirm_dialog, null);
        TextView textViewMessage = dialogView.findViewById(R.id.confirmation_text);
        textViewMessage.setText(message);

        builder.setView(dialogView)
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onConfirm();
                        }
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

