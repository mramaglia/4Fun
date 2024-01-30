package com.example.fansfun.entities;

import java.util.Date;

public class Evento {

    private String foto;
    private String nome;
    private String descrizione;
    private Date data;

    public Evento() {
    }

    public Evento(String foto, String nome, String descrizione, Date data) {
        this.foto = foto;
        this.nome = nome;
        this.descrizione = descrizione;
        this.data = data;
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
}
