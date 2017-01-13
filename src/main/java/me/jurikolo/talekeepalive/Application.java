package me.jurikolo.talekeepalive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {

            String propPath = "resources/auth.properties";

            TaleProperties properties = new TaleProperties();
            properties.getPropValues(propPath);
            log.info(properties.getPropValues(propPath).getId());

            log.info("Execute GET request in order to fetch CSRFToken");
            ResponseEntity<Object> responseEntity = restTemplate.getForEntity(
                    "http://the-tale.org/game/api/info?api_version=1.6&api_client=jurikolo-1&account=" + properties.getPropValues(propPath).getId(), Object.class);

            String body = responseEntity.getBody().toString();
            log.debug(body);
            if (body.contains("alive=true")) {
                log.info("Hero is alive");
            } else {
                String sessionid = "";
                String csrftoken = "";
                for (String cookie : responseEntity.getHeaders().get("Set-Cookie")) {
                    if (cookie.startsWith("sessionid=")) {
                        sessionid = cookie.substring(0, cookie.indexOf(";"));
                    }
                    if (cookie.startsWith("csrftoken=")) {
                        csrftoken = cookie.substring(0, cookie.indexOf(";"));
                    }
                }

                log.debug("Session id: " + sessionid);
                log.debug("CSRF token: " + csrftoken);

                log.info("Now it's time to authorize");
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("Cookie", csrftoken);
                headers.add("X-CSRFToken", csrftoken.replace("csrftoken=", ""));

                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                map.add("email", properties.getPropValues(propPath).getUsername());
                map.add("password", properties.getPropValues(propPath).getPassword());

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

                ResponseEntity<String> response = restTemplate.postForEntity("http://the-tale.org/accounts/auth/api/login?api_version=1.0&api_client=jurikolo-1", request, String.class);
                log.debug(response.toString());

                for (String cookie : response.getHeaders().get("Set-Cookie")) {
                    if (cookie.startsWith("sessionid=")) {
                        sessionid = cookie.substring(0, cookie.indexOf(";"));
                    }
                    if (cookie.startsWith("csrftoken=")) {
                        csrftoken = cookie.substring(0, cookie.indexOf(";"));
                    }
                }

                log.debug("Session id: " + sessionid);
                log.debug("CSRF token: " + csrftoken);

                HttpHeaders headers2 = new HttpHeaders();
                headers2.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("Cookie", sessionid);
                headers2.add("Cookie", csrftoken);
                headers2.add("X-CSRFToken", csrftoken.replace("csrftoken=", ""));

                MultiValueMap<String, String> map2 = new LinkedMultiValueMap<String, String>();
                map2.add("email", "jurikolo@yandex.com");
                map2.add("password", "66PAtEb6lzCJQJc8");

                HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<MultiValueMap<String, String>>(map, headers);

                ResponseEntity<String> response2 = restTemplate.postForEntity("http://the-tale.org/game/abilities/help/api/use?api_version=1.0&api_client=jurikolo-1&account=" + properties.getPropValues(propPath).getId(), request2, String.class);
                log.info(response2.toString());

            }
        };
    }
}