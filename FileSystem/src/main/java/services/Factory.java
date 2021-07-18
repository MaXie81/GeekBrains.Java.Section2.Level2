package services;

import dictionary.MessageTypes;
import domain.*;
import factory.PropertiesService;
import filesystem.Directory;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class Factory {
    private static final String PROPERTIES_URL = "properties/directory.properties";
    
    public static Directory getDirectory(String dirPath, boolean flgStartPath) {
        return new Directory(dirPath, flgStartPath);
    }
    public static Properties getProperties() {
        Factory factory = new Factory();
        return PropertiesService.getProperties(factory.getURL());
    }
    public static HashMap<MessageTypes, DirectoryAction> getMapDirectoryAction(Directory directory) {
        HashMap<MessageTypes, DirectoryAction> mapDirectoryAction = new HashMap<>();

        mapDirectoryAction.put(MessageTypes.DIR_INFO, new Info(directory));
        mapDirectoryAction.put(MessageTypes.DIR_SET, new Set(directory));
        mapDirectoryAction.put(MessageTypes.FILE_ADD, new CreateDirectory(directory));
        mapDirectoryAction.put(MessageTypes.DIR_DEL, new Delete(directory));
        mapDirectoryAction.put(MessageTypes.DIR_COPY, new AddFile(directory));

        return mapDirectoryAction;
    }
    private URL getURL() {
        return getClass().getClassLoader().getResource(PROPERTIES_URL);
    }
}
