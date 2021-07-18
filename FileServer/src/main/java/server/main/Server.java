package server.main;

import server.services.Factory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {
    private final int PORT;

    public Server() {
        Properties properties = Factory.getProperties();
        PORT = Integer.parseInt(properties.getProperty("PORT"));
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен. Порт: " + serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                Factory.getClientHandler(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
