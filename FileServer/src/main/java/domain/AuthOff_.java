package domain;

import fileserver.ClientHandler;
import message.Mess;
import message.MessUtil;

public class AuthOff_ implements ServerAction {
    private ClientHandler client;

    public AuthOff_(ClientHandler client) {
        this.client = client;
    }

    @Override
    public Mess action(Mess mess) {
        client.setIsAuth(false);
        return MessUtil.getRespOk(mess);
    }
}
