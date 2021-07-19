package client.services;

import client.domain.*;
import client.main.Client;
import dictionary.MessageTypes;
import factory.PropertiesService;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class Factory {
    private static final String PROPERTIES_URL = "properties/client.properties";

    public static Client getClient() {
        return new Client();
    }
    public static Properties getProperties() {
        Factory factory = new Factory();
        return PropertiesService.getProperties(factory.getURL());
    }
    public static HashMap<MessageTypes, ClientAction> getMapClientAction(Client client) {
        HashMap<MessageTypes, ClientAction> mapClientAction = new HashMap<>();

        mapClientAction.put(MessageTypes.AUTH_ON, new AuthOn(client));
        mapClientAction.put(MessageTypes.AUTH_OFF, new AuthOff(client));
        mapClientAction.put(MessageTypes.CLOSE_CONNECTION, new Disconn(client));
        mapClientAction.put(MessageTypes.GET_DIRECTORY, new RouteMess(client));
        mapClientAction.put(MessageTypes.SET_DIRECTORY, new RouteMess(client));
        mapClientAction.put(MessageTypes.ADD_DIRECTORY, new RouteMess(client));
        mapClientAction.put(MessageTypes.DELETE, new RouteMess(client));
        mapClientAction.put(MessageTypes.COPY_FILE, new CopyFile(client));

        return mapClientAction;
    }
    private URL getURL() {
        return getClass().getClassLoader().getResource(PROPERTIES_URL);
    }
}
