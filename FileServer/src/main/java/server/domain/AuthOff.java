package server.domain;

import server.main.ClientHandler;
import message.Mess;
import message.MessUtil;

public class AuthOff implements ServerAction {
    private ClientHandler client;

    public AuthOff(ClientHandler client) {
        this.client = client;
    }

    @Override
    public Mess action(Mess mess) {
        client.setIsAuth(false);
        return MessUtil.getRespOk(mess);
    }
}