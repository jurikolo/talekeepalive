package me.jurikolo.talekeepalive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jurikolo on 16.01.17.
 */
@JsonIgnoreProperties
public class Account {

    private String id;
    private Hero hero;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", hero=" + hero +
                '}';
    }
}
