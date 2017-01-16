package me.jurikolo.talekeepalive.model;

/**
 * Created by jurikolo on 16.01.17.
 */

public class Energy {

    private String max;
    private String value;

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Energy{" +
                "max='" + max + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
