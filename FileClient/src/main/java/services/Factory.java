package services;

import factory.PropertiesService;
import filesystem.Directory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Factory {
    private static final String PROPERTIES_URL = "properties/client.properties";
    private static CommunicationService communicationService;

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
        if (communicationService == null) communicationService = new CommunicationService(getDirectory());
        return communicationService;
    }

    private URL getURL() {
        return getClass().getClassLoader().getResource(PROPERTIES_URL);
    }
}
