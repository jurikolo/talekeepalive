package me.jurikolo.talekeepalive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jurikolo on 16.01.17.
 */
@JsonIgnoreProperties
public class Hero {

    private String id;
    private Energy energy;
    private Action action;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Energy getEnergy() {
        return energy;
    }

    public void setEnergy(Energy energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "Hero{" +
                "id='" + id + '\'' +
                ", action=" + action +
                ", energy=" + energy +
                '}';
    }
}
