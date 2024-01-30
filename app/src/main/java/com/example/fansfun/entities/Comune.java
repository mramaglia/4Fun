package com.example.fansfun.entities;

public class Comune {

    private String name;
    private String provincia;
    private String regione;
    private String comuneId;

    public Comune(){ }

    public Comune(String name, String provincia, String regione, String comuneId){
        this.name = name;
        this.provincia = provincia;
        this.regione = regione;
        this.comuneId = comuneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getComuneId() {
        return comuneId;
    }

    public void setComuneId(String comuneId) {
        this.comuneId = comuneId;
    }
}
