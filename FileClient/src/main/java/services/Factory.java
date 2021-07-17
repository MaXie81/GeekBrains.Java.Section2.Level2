package services;

import dictionary.MessageTypes;
import domain.*;
import factory.PropertiesService;
import filesystem.Directory;
import main.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class Factory {
    private static final String PROPERTIES_URL = "properties/client.properties";
    private static HashMap<MessageTypes, ClientAction> mapClientAction;
    private static CommunicationService communicationService;
    private static Client client;

    public static Client getClient() {
        if (client == null) client = new Client();
        return client;
    }
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
    public static HashMap<MessageTypes, ClientAction> getMapClientAction(Client client) {
        if (mapClientAction == null) {
            mapClientAction = new HashMap<>();
            mapClientAction.put(MessageTypes.AUTH_ON, new AuthOn(client));
            mapClientAction.put(MessageTypes.AUTH_OFF, new AuthOff(client));
            mapClientAction.put(MessageTypes.CONN_CLOSE, new Disconn(client));
            mapClientAction.put(MessageTypes.DIR_INFO, new RouteMess(client));
            mapClientAction.put(MessageTypes.DIR_SET, new RouteMess(client));
            mapClientAction.put(MessageTypes.FILE_ADD, new RouteMess(client));
            mapClientAction.put(MessageTypes.DIR_DEL, new RouteMess(client));
            mapClientAction.put(MessageTypes.DIR_COPY, new CopyFile(client));
        }
        return mapClientAction;
    }
    private URL getURL() {
        return getClass().getClassLoader().getResource(PROPERTIES_URL);
    }
}
