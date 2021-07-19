package server.services;

import dictionary.MessageTypes;
import dictionary.ResultCodes;
import factory.PropertiesService;
import server.domain.*;
import server.main.ClientHandler;

import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class Factory {
    private static final String PROPERTIES_URL = "properties/server.properties";
    private static final String LOGIN_URL = "properties/login.pass";

    public static ClientHandler getClientHandler(Socket socket) {
        return new ClientHandler(socket);
    }
    public static Properties getProperties() {
        Factory factory = new Factory();
        return PropertiesService.getProperties(factory.getURL(PROPERTIES_URL));
    }
    public static ResultCodes authLoginPassword(String login, String password) {
        Factory factory = new Factory();
        Properties properties = PropertiesService.getProperties(factory.getURL(LOGIN_URL));

        String authPassword =  properties.getProperty(login);
        if (authPassword == null) return ResultCodes.ERR_LOGIN;
        if (!authPassword.equals(password)) return ResultCodes.ERR_PASSWORD;
        return ResultCodes.OK;
    }
    public static HashMap<MessageTypes, ServerAction> getMapClientAction(ClientHandler client) {
        HashMap<MessageTypes, ServerAction> mapClientAction = new HashMap<>();

        mapClientAction.put(MessageTypes.AUTH_ON, new AuthOn(client));
        mapClientAction.put(MessageTypes.AUTH_OFF, new AuthOff(client));
        mapClientAction.put(MessageTypes.CLOSE_CONNECTION, new CloseConnect(client));
        mapClientAction.put(MessageTypes.GET_DIRECTORY, new RouteMess(client));
        mapClientAction.put(MessageTypes.SET_DIRECTORY, new RouteMess(client));
        mapClientAction.put(MessageTypes.ADD_DIRECTORY, new RouteMess(client));
        mapClientAction.put(MessageTypes.DELETE, new RouteMess(client));
        mapClientAction.put(MessageTypes.COPY_FILE, new CopyFile(client));

        return mapClientAction;
    }
    private URL getURL(String resource) {
        return getClass().getClassLoader().getResource(resource);
    }
}
