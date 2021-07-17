package services;

import factory.PropertiesService;
import filesystem.Directory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Factory {
    private static final String PROPERTIES_URL = "properties/client.properties";

    public static Properties getProperties() {
        Factory factory = new Factory();
        return PropertiesService.getProperties(factory.getURL());
    }
    public static Directory getDirectory() {
        Properties properties = Factory.getProperties();

        try {
            return new Directory(properties.getProperty("PATH_START"), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static CommunicationService getCommunicationService() {
        return new CommunicationService(getDirectory());
    }

    private URL getURL() {
        return getClass().getClassLoader().getResource(PROPERTIES_URL);
    }
}
