package domain;

import fileserver.ClientHandler;
import message.Mess;
import message.MessUtil;

public class Close_ implements ServerAction {
    private ClientHandler client;

    public Close_(ClientHandler client) {
        this.client = client;
    }

    @Override
    public Mess action(Mess mess) {
        return MessUtil.getRespOk(mess);
    }
}
