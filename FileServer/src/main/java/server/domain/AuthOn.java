package server.domain;

import dictionary.ResultCodes;
import server.main.ClientHandler;
import message.Mess;
import message.MessUtil;
import server.services.Factory;

public class AuthOn implements ServerAction {
    private ClientHandler client;

    public AuthOn(ClientHandler client) {
        this.client = client;
    }

    @Override
    public Mess action(Mess mess) {
        ResultCodes code = Factory.authLoginPassword(mess.getLogin(), mess.getPassword());
        client.setIsAuth(code == ResultCodes.OK);

        if (client.isAuth()) client.getCommunication().setDirectory(mess.getLogin());

        return MessUtil.getResp(mess, code);
    }
}
