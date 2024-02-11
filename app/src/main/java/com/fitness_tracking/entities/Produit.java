package com.fitness_tracking.entities;

public class Produit {

    Long id;

    String name;

    double calorie;

    double proteine;

    double carbe;

    double fate;

    Long idUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getProteine() {
        return proteine;
    }

    public void setProteine(double proteine) {
        this.proteine = proteine;
    }

    public double getCarbe() {
        return carbe;
    }

    public void setCarbe(double carbe) {
        this.carbe = carbe;
    }

    public double getFate() {
        return fate;
    }

    public void setFate(double fate) {
        this.fate = fate;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Produit(Long id, String name, double calorie, double proteine, double carbe, double fate, Long idUser) {
        this.id = id;
        this.name = name;
        this.calorie = calorie;
        this.proteine = proteine;
        this.carbe = carbe;
        this.fate = fate;
        this.idUser = idUser;
    }

    public Produit() {
        super();
    }

    public String toString() {
        return this.getName();
    }
}
