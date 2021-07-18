package domain;

import fileserver.ClientHandler;
import message.Mess;

public class RouteMess_ implements ServerAction {
    private ClientHandler client;

    public RouteMess_(ClientHandler client) {
        this.client = client;
    }

    @Override
    public Mess action(Mess mess) {
        return client.getCommunication().sendLocal(mess);
    }
}
