package me.jurikolo.talekeepalive.service;

import me.jurikolo.talekeepalive.model.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jurikolo on 12.01.17.
 */
public class PropertiesService {
    private static final Logger log = LoggerFactory.getLogger(PropertiesService.class);

    InputStream inputStream;

    public Auth getPropValues(String propFileName) {
        Auth result = new Auth();

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propFileName)) {
            properties.load(input);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        result.setUsername(properties.getProperty("username"));
        result.setPassword(properties.getProperty("password"));
        result.setId(properties.getProperty("id"));

        return result;
    }
}
