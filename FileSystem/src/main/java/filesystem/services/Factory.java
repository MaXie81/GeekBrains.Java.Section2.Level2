package filesystem.services;

import dictionary.MessageTypes;
import filesystem.domain.*;
import factory.PropertiesService;
import filesystem.main.Directory;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class Factory {
    private static final String PROPERTIES_URL = "properties/directory.properties";
    
    public static Directory getDirectory(String dirPath) {
        return new Directory(dirPath);
    }
    public static Properties getProperties() {
        Factory factory = new Factory();
        return PropertiesService.getProperties(factory.getURL());
    }
    public static HashMap<MessageTypes, DirectoryAction> getMapDirectoryAction(Directory directory) {
        HashMap<MessageTypes, DirectoryAction> mapDirectoryAction = new HashMap<>();

        mapDirectoryAction.put(MessageTypes.GET_DIRECTORY, new GetInfo(directory));
        mapDirectoryAction.put(MessageTypes.SET_DIRECTORY, new Set(directory));
        mapDirectoryAction.put(MessageTypes.ADD_DIRECTORY, new CreateDirectory(directory));
        mapDirectoryAction.put(MessageTypes.DELETE, new Delete(directory));
        mapDirectoryAction.put(MessageTypes.COPY_FILE, new AddFile(directory));

        return mapDirectoryAction;
    }
    private URL getURL() {
        return getClass().getClassLoader().getResource(PROPERTIES_URL);
    }
}
