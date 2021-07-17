package domain;

import dictionary.ResultCodes;
import main.Client;
import message.Mess;
import message.MessUtil;

public class AuthOff implements ClientAction {
    private Client client;

    public AuthOff(Client client) {
        this.client = client;
    }
    @Override
    public Mess action() {
        Mess messResp = client.getCommunication().sendRemote(client.getMess());

        if (MessUtil.isRespOK(client.getMess(), messResp)) {
            client.setIsAuth(false);
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR);
        }
        return messResp;
    }
}
