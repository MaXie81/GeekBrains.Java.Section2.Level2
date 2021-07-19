package factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesService {
    private Properties properties = null;

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
}
