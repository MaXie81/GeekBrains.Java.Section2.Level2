package client.domain;

import dictionary.ResultCodes;
import client.main.Client;
import message.Mess;
import message.MessUtil;

public class AuthOff implements ClientAction {
    private Client client;

    public AuthOff(Client client) {
        this.client = client;
    }
    @Override
    public Mess action(Mess mess) {
        Mess messResp = client.getCommunication().sendRemote(mess);

        if (MessUtil.isRespOK(mess, messResp)) {
            client.setIsAuth(false);
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR);
        }
        return messResp;
    }
}
