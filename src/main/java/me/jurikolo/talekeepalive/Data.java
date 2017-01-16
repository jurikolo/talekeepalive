package me.jurikolo.talekeepalive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jurikolo on 16.01.17.
 */
@JsonIgnoreProperties
public class Data {

    private String mode;
    private Account account;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Data{" +
                "mode='" + mode + '\'' +
                ", account=" + account +
                '}';
    }
}
