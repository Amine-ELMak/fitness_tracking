package com.fitness_tracking.entities;

public class Exercice {

    Long id;

    String name;

    String path;

    String description;

    Long idUser;

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Exercice(Long id, String name, String path, String description, Long idUser) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.description = description;
        this.idUser = idUser;
    }

    public Exercice() {
        super();
    }

    public String toString() {
        return this.getName();
    }

}
