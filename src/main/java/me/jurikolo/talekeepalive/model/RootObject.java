package me.jurikolo.talekeepalive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jurikolo on 16.01.17.
 */
@JsonIgnoreProperties
public class RootObject {

    private String status;
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RootObject{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
