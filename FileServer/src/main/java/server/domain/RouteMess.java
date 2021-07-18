package server.domain;

import server.main.ClientHandler;
import message.Mess;

public class RouteMess implements ServerAction {
    private ClientHandler client;

    public RouteMess(ClientHandler client) {
        this.client = client;
    }

    @Override
    public Mess action(Mess mess) {
        return client.getCommunication().sendLocal(mess);
    }
}
