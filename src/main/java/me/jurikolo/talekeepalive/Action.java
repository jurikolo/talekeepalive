package me.jurikolo.talekeepalive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jurikolo on 16.01.17.
 */
@JsonIgnoreProperties
public class Action {

    private String description;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Action{" +
                "description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
