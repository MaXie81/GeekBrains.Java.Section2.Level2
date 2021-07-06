package main;

import dictionary.ResultCodes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private final int PORT = 6005;
    private static final HashMap<String, String> MAP_LOGIN_PASSWORD;
    public static ResultCodes authLoginPassword(String login, String password) {
        if (!MAP_LOGIN_PASSWORD.containsKey(login)) return ResultCodes.ERR_LOGIN;
        if (!password.equals(MAP_LOGIN_PASSWORD.get(login))) return ResultCodes.ERR_PASSWORD;
        return ResultCodes.OK;
    }

    static {
        HashMap<String, String> mapLogPass = new HashMap<>();
        mapLogPass.put("User1", "Pass1");
        mapLogPass.put("User2", "Pass2");
        mapLogPass.put("User3", "Pass3");

        MAP_LOGIN_PASSWORD = new HashMap<>(mapLogPass);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен. Порт: " + serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
