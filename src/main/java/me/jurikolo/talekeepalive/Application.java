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

import java.util.Map;

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

            log.info("Execute GET request in order to fetch CSRFToken");
            ResponseEntity<Object> responseEntity = restTemplate.getForEntity(
                    "http://the-tale.org/api/info?api_version=1.0&api_client=jurikolo-1", Object.class);

            String sessionid = "";
            String csrftoken = "";
            for (String cookie: responseEntity.getHeaders().get("Set-Cookie")) {
                if (cookie.startsWith("sessionid=")) {
                    sessionid = cookie.substring(0, cookie.indexOf(";"));
                }
                if (cookie.startsWith("csrftoken=")) {
                    csrftoken = cookie.substring(0, cookie.indexOf(";"));
                }
            }

            log.info("Now it's time to authorize");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //headers.add("Cookie", sessionid);
            headers.add("Cookie", csrftoken);
            headers.add("X-CSRFToken", csrftoken.replace("csrftoken=",""));

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("email", "jurikolo@yandex.com");
            map.add("password", "Привет Андрей");
            //map.add("remember", "false");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity( "http://the-tale.org/accounts/auth/api/login?api_version=1.0&api_client=jurikolo-1", request , String.class );
            log.info(response.toString());
        };
    }
}