package domain;

import main.Client;
import message.Mess;

public class RouteMess implements ClientAction {
    private Client client;

    public RouteMess(Client client) {
        this.client = client;
    }
    @Override
    public Mess action() {
        return client.getCommunication().send(client.getMess());
    }
}
