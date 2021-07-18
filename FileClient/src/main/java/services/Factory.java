package services;

import dictionary.MessageTypes;
import domain.*;
import factory.PropertiesService;
import main.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class Factory {
    private static final String PROPERTIES_URL = "properties/client.properties";
    private static Client client;

    public static Client getClient() {
        if (client == null) client = new Client();
        return client;
    }
    public static Properties getProperties() {
        Factory factory = new Factory();
        return PropertiesService.getProperties(factory.getURL());
    }
    public static HashMap<MessageTypes, ClientAction> getMapClientAction(Client client) {
        HashMap<MessageTypes, ClientAction> mapClientAction = new HashMap<>();

        mapClientAction.put(MessageTypes.AUTH_ON, new AuthOn(client));
        mapClientAction.put(MessageTypes.AUTH_OFF, new AuthOff(client));
        mapClientAction.put(MessageTypes.CONN_CLOSE, new Disconn(client));
        mapClientAction.put(MessageTypes.DIR_INFO, new RouteMess(client));
        mapClientAction.put(MessageTypes.DIR_SET, new RouteMess(client));
        mapClientAction.put(MessageTypes.FILE_ADD, new RouteMess(client));
        mapClientAction.put(MessageTypes.DIR_DEL, new RouteMess(client));
        mapClientAction.put(MessageTypes.DIR_COPY, new CopyFile(client));

        return mapClientAction;
    }
    private URL getURL() {
        return getClass().getClassLoader().getResource(PROPERTIES_URL);
    }
}
