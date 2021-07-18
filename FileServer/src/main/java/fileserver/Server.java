package fileserver;

import services.Factory_;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int PORT = 6005;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен. Порт: " + serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                Factory_.getClientHandler(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
