package main;

import dictionary.ResultCodes;
import message.Mess;
import message.MessUtil;
import services.Factory;

public class AuthOff implements ClientAction {
    private Client client;

    public AuthOff(Client client) {
        this.client = client;
    }
    @Override
    public Mess action() {
        Mess messResp = Factory.getCommunicationService().sendRemote(client.getMess());

        if (MessUtil.isRespOK(client.getMess(), messResp)) {
            client.setIsAuth(false);
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR);
        }
        return messResp;
    }
}
