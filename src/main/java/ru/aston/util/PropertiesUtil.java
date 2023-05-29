package ru.aston.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Properties;

@UtilityClass
public class PropertiesUtil {

    private static final String PROPERTIES_FILE_NAME = "db.properties";
    private static final Properties PROPERTIES;

    static {
        PROPERTIES = new Properties();
        loadProperties(PROPERTIES_FILE_NAME);
    }

    public String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties(String propertiesFileName) {
        try (var inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream(propertiesFileName)) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
