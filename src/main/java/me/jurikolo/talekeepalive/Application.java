package me.jurikolo.talekeepalive;

import me.jurikolo.talekeepalive.model.Auth;
import me.jurikolo.talekeepalive.model.RootObject;
import me.jurikolo.talekeepalive.model.WebHeaders;
import me.jurikolo.talekeepalive.service.PropertiesService;
import me.jurikolo.talekeepalive.service.WebHeadersService;
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

import java.io.File;

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
    public CommandLineRunner run(RestTemplate restTemplate) {
        return args -> {
            String propPath = "resources";
            //get list of files
            File dir = new File(propPath);
            String[] files = dir.list();
            if (files.length == 0) {
                log.error("Resource directory is empty");
            } else {
                PropertiesService properties = new PropertiesService();
                for (String file : files) {
                    Auth auth = properties.getPropValues(propPath + "/" + file);
                    log.info("Processing " + auth.getUsername() + ", file: " + file);
                    doExec(restTemplate, auth);
                }
            }
        };
    }

    private void doExec(RestTemplate restTemplate, Auth auth) {
        //Execute GET request to fetch hero information
        RootObject rootObject = restTemplate.getForObject(
                "http://the-tale.org/game/api/info?api_version=1.6&api_client=jurikolo-1&account=" + auth.getId(), RootObject.class);

        try {
            if(rootObject.getData().getAccount().getHero().getAction().getType().equals("0") ||
                    rootObject.getData().getAccount().getHero().getAction().getType().equals("4")) {
                log.info("Rule hit with data: " + rootObject.getData().getAccount().toString());
                ResponseEntity<Object> objectResponseEntity = restTemplate.getForEntity(
                        "http://the-tale.org/game/api/info?api_version=1.6&api_client=jurikolo-1&account=" + auth.getId(), Object.class);
                WebHeadersService webHeadersService = new WebHeadersService();
                WebHeaders webHeaders = webHeadersService.getHeadersByObject(objectResponseEntity);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("Cookie", webHeaders.getCsrftoken());
                headers.add("X-CSRFToken", webHeaders.getCsrftoken().replace("csrftoken=", ""));

                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("email", auth.getUsername());
                map.add("password", auth.getPassword());

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

                ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://the-tale.org/accounts/auth/api/login?api_version=1.0&api_client=jurikolo-1", request, String.class);
                webHeaders = webHeadersService.getHeadersByString(stringResponseEntity);

                headers.add("Cookie", webHeaders.getSessionid());

                HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<>(map, headers);

                ResponseEntity<String> response2 = restTemplate.postForEntity("http://the-tale.org/game/abilities/help/api/use?api_version=1.0&api_client=jurikolo-1&account=" + auth.getId(), request2, String.class);
                log.info(response2.toString());
            } else {
                log.info("Nothing to do");
            }
        } catch(Exception e) {
            log.error("Exception: " + e);
        }
        log.info("-----");
    }
}