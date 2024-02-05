package com.example.fansfun.entities;

import java.util.Date;

public class Evento {

    private String foto;
    private String nome;
    private String descrizione;
    private Date data;
    private String organizzatore;
    private String luogo;
    private int maxPartecipanti;
    private int numPartecipanti;
    private String categoria;
    private String indirizzo;

    public Evento() {
        numPartecipanti=0;
    }

    public Evento(String foto, String nome, String descrizione, Date data, String organizzatore, String luogo, int maxPartecipanti, int numPartecipanti, String categoria, String indirizzo) {
        this.foto = foto;
        this.nome = nome;
        this.descrizione = descrizione;
        this.data = data;
        this.organizzatore = organizzatore;
        this.luogo = luogo;
        this.maxPartecipanti = maxPartecipanti;
        this.numPartecipanti = numPartecipanti;
        this.categoria = categoria;
        this.indirizzo = indirizzo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getOrganizzatore() {
        return organizzatore;
    }

    public void setOrganizzatore(String organizzatore) {
        this.organizzatore = organizzatore;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public int getMaxPartecipanti() {
        return maxPartecipanti;
    }

    public void setMaxPartecipanti(int maxPartecipanti) {
        this.maxPartecipanti = maxPartecipanti;
    }

    public int getNumPartecipanti() {
        return numPartecipanti;
    }

    public void setNumPartecipanti(int numPartecipanti) {
        this.numPartecipanti = numPartecipanti;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
}
