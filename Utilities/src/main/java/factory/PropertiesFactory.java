package factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesFactory {
    private final String PATH_CLIENT = "properties/client.properties";
    private final String PATH_SERVER = "properties/server.properties";
    private Properties properties = null;
    public static Properties getProperties(boolean isClient) {
        try {
            PropertiesFactory propertiesFactory = new PropertiesFactory();
            propertiesFactory.properties = new Properties();
            propertiesFactory.properties.load(new FileReader(new File(propertiesFactory.getFile(isClient))));

            return propertiesFactory.properties;
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
