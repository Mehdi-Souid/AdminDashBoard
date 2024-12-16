package com.example.admindashboard;

import java.util.Date;


public class Flight {

    String numero;
    Date date;
    String destination;
    String depart;
    double price;

    public Flight(String numero, Date date, String destination, String depart, Double price) {
        this.numero = numero;
        this.date = date;
        this.destination = destination;
        this.depart = depart;
        this.price = price;
    }


    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }
}
