package main;

import message.Mess;
import services.CommunicationService;
import services.Factory;

public class RouteMess implements ClientAction {
    private Client client;
    private CommunicationService communicationService;

    public RouteMess(Client client) {
        this.client = client;
        this.communicationService = Factory.getCommunicationService();
    }
    @Override
    public Mess action() {
        return communicationService.send(client.getMess());
    }
}
