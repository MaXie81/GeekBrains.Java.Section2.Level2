package main;

import dictionary.ResultCodes;
import message.Mess;
import message.MessUtil;
import services.CommunicationService;
import services.Factory;

public class AuthOff implements ClientAction {
    private Client client;
    private CommunicationService communicationService;

    public AuthOff(Client client) {
        this.client = client;
        this.communicationService = Factory.getCommunicationService();
    }
    @Override
    public Mess action() {
        Mess messResp = communicationService.sendRemote(client.getMess());

        if (MessUtil.isRespOK(client.getMess(), messResp)) {
            client.setIsAuth(false);
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR);
        }
        return messResp;
    }
}
