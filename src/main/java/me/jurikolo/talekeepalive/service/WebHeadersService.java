package me.jurikolo.talekeepalive.service;

import me.jurikolo.talekeepalive.model.WebHeaders;
import org.springframework.http.ResponseEntity;

/**
 * Created by jurikolo on 16.01.17.
 */
public class WebHeadersService {
    public WebHeaders getHeadersByObject(ResponseEntity<Object> responseEntity) {
        WebHeaders webHeaders = new WebHeaders();
        for (String cookie : responseEntity.getHeaders().get("Set-Cookie")) {
            if (cookie.startsWith("sessionid=")) {
                webHeaders.setSessionid(cookie.substring(0, cookie.indexOf(";")));
            }
            if (cookie.startsWith("csrftoken=")) {
                webHeaders.setCsrftoken(cookie.substring(0, cookie.indexOf(";")));
            }
        }
        return webHeaders;
    }

    public WebHeaders getHeadersByString(ResponseEntity<String> responseEntity) {
        WebHeaders webHeaders = new WebHeaders();
        for (String cookie : responseEntity.getHeaders().get("Set-Cookie")) {
            if (cookie.startsWith("sessionid=")) {
                webHeaders.setSessionid(cookie.substring(0, cookie.indexOf(";")));
            }
            if (cookie.startsWith("csrftoken=")) {
                webHeaders.setCsrftoken(cookie.substring(0, cookie.indexOf(";")));
            }
        }
        return webHeaders;
    }
}
