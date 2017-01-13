package me.jurikolo.talekeepalive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jurikolo on 12.01.17.
 */
public class TaleProperties {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    InputStream inputStream;

    public Auth getPropValues(String propFileName) throws IOException {
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
