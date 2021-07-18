package server.domain;

import server.main.ClientHandler;
import message.Mess;
import message.MessUtil;

public class Close implements ServerAction {
    private ClientHandler client;

    public Close(ClientHandler client) {
        this.client = client;
    }

    @Override
    public Mess action(Mess mess) {
        return MessUtil.getRespOk(mess);
    }
}
