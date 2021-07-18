package domain;

import dictionary.ResultCodes;
import fileserver.ClientHandler;
import filesystem.Directory;
import message.Mess;
import message.MessUtil;
import services.Factory_;

import java.io.FileNotFoundException;

public class AuthOn_ implements ServerAction {
    private ClientHandler client;

    public AuthOn_(ClientHandler client) {
        this.client = client;
    }

    @Override
    public Mess action(Mess mess) {
        ResultCodes code = Factory_.authLoginPassword(mess.getLogin(), mess.getPassword());
        client.setIsAuth(code == ResultCodes.OK);

        return MessUtil.getResp(mess, code);
    }

}
