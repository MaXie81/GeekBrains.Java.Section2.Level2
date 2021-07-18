package client.domain;

import client.main.Client;
import message.Mess;

public class RouteMess implements ClientAction {
    private Client client;

    public RouteMess(Client client) {
        this.client = client;
    }
    @Override
    public Mess action(Mess mess) {
        return client.getCommunication().send(mess);
    }
}
