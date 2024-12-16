package com.example.admindashboard;

import java.util.Date;

public class Ticket {
    String idt,iduser,idflight,destination,depart,classe;
    Date date;
    Double price;

    public String getIdt() {
        return idt;
    }

    public void setIdt(String idt) {
        this.idt = idt;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getIdflight() {
        return idflight;
    }

    public void setIdflight(String idflight) {
        this.idflight = idflight;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Ticket(String idt, String iduser, String idflight, String destination, String depart, String classe, Date date, Double price) {
        this.idt = idt;
        this.iduser = iduser;
        this.idflight = idflight;
        this.destination = destination;
        this.depart = depart;
        this.classe = classe;
        this.date = date;
        this.price = price;
    }
}
