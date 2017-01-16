package me.jurikolo.talekeepalive;

/**
 * Created by jurikolo on 16.01.17.
 */
public class WebHeaders {
    private String sessionid;
    private String csrftoken;

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getCsrftoken() {
        return csrftoken;
    }

    public void setCsrftoken(String csrftoken) {
        this.csrftoken = csrftoken;
    }
}
