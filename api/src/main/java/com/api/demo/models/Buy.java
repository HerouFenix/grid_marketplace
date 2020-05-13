package com.api.demo.models;

import javax.persistence.*;
import java.util.Date;


@Entity
public class Buy {

    @Id
    @GeneratedValue( strategy= GenerationType.AUTO )
    private int id;

    @ManyToOne
    private Sell sell;

    @ManyToOne
    private User user;

    @Temporal(TemporalType.DATE)
    private Date date;


    public int getId() {
        return id;
    }

    public void setId(int buyId) {
        this.id = buyId;
    }

    public Sell getSell() {
        return sell;
    }

    public void setSell(Sell sell) {
        this.sell = sell;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return (Date) date.clone();
    }

    public void setDate(Date date) {
        this.date = (Date) date.clone();
    }
}