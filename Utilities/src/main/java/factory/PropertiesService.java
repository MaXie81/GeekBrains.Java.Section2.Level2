package factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesService {
    private final String PATH_CLIENT = "properties/client.properties";
    private final String PATH_SERVER = "properties/server.properties";
    private Properties properties = null;
    public static Properties getProperties(boolean isClient) {
        try {
            PropertiesService propertiesService = new PropertiesService();
            propertiesService.properties = new Properties();
            propertiesService.properties.load(new FileReader(new File(propertiesService.getFile(isClient))));

            return propertiesService.properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Properties getProperties(URL url) {
        try {
            PropertiesService propertiesService = new PropertiesService();
            propertiesService.properties = new Properties();
            propertiesService.properties.load(new FileReader(new File(url.getFile())));

            return propertiesService.properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getFile(boolean isClient) {
        URL url = getClass().getClassLoader().getResource(isClient ? PATH_CLIENT : PATH_SERVER);
        return url.getFile();
    }
}
