package com.fitness_tracking.entities;

import java.util.Date;

public class Steps {
    Long id;

    Date date;

    int step;
    Long idUser;

    public Steps() {
        super();
    }

    public Steps(Long id, Date date, int step, Long idUser) {
        this.id = id;
        this.date = date;
        this.step = step;
        this.idUser = idUser;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getStep() {
        return step;
    }


}
