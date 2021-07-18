package domain;

import dictionary.ResultCodes;
import main.Client;
import message.Mess;
import message.MessUtil;

public class Disconn implements ClientAction {
    private Client client;

    public Disconn(Client client) {
        this.client = client;
    }
    @Override
    public Mess action(Mess mess) {
        Mess messResp = client.getCommunication().sendRemote(mess);

        if (MessUtil.isRespOK(mess, messResp)) {
            client.setIsAuth(false);
            client.getCommunication().closeConnection();
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR);
        }
        return messResp;
    }
}
