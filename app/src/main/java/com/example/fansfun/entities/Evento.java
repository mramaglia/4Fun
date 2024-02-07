package com.example.fansfun.entities;

import java.io.Serializable;
import java.util.Date;

public class Evento implements Serializable {

    private String foto;
    private String nome;
    private String descrizione;
    private Date data;
    private String organizzatore;
    private String luogo;
    private String categoria;
    private String indirizzo;
    private String id;

    public Evento() {
    }

    public Evento(String foto, String nome, String descrizione, Date data, String organizzatore, String luogo, String categoria, String indirizzo) {
        this.foto = foto;
        this.nome = nome;
        this.descrizione = descrizione;
        this.data = data;
        this.organizzatore = organizzatore;
        this.luogo = luogo;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
