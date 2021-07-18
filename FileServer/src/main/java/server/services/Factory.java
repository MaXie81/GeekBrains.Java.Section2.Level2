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
    private static final HashMap<String, String> MAP_LOGIN_PASSWORD;
    static {
        HashMap<String, String> mapLogPass = new HashMap<>();
        mapLogPass.put("User1", "Pass1");
        mapLogPass.put("User2", "Pass2");
        mapLogPass.put("User3", "Pass3");

        MAP_LOGIN_PASSWORD = new HashMap<>(mapLogPass);
    }

    public static ClientHandler getClientHandler(Socket socket) {
        return new ClientHandler(socket);
    }
    public static Properties getProperties() {
        Factory factory = new Factory();
        return PropertiesService.getProperties(factory.getURL());
    }
    public static ResultCodes authLoginPassword(String login, String password) {
        if (!MAP_LOGIN_PASSWORD.containsKey(login)) return ResultCodes.ERR_LOGIN;
        if (!password.equals(MAP_LOGIN_PASSWORD.get(login))) return ResultCodes.ERR_PASSWORD;
        return ResultCodes.OK;
    }
    public static HashMap<MessageTypes, ServerAction> getMapClientAction(ClientHandler client) {
        HashMap<MessageTypes, ServerAction> mapClientAction = new HashMap<>();

        mapClientAction.put(MessageTypes.AUTH_ON, new AuthOn(client));
        mapClientAction.put(MessageTypes.AUTH_OFF, new AuthOff(client));
        mapClientAction.put(MessageTypes.CONN_CLOSE, new Close(client));
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
