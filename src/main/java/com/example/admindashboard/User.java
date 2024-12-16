package com.example.admindashboard;

import java.awt.*;

public class User {
    String id;
    String nom;
    String prenom;
    String password;
     Number age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Number getAge() {
        return age;
    }

    public void setAge(Number age) {
        this.age = age;
    }

    public User(String id, String nom, String prenom, String password, Number age) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.age = age;
    }
    public User(String id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.password = "";
        this.age = 0;
    }
}
