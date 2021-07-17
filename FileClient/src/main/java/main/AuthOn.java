package main;

import dictionary.ResultCodes;
import message.Mess;
import message.MessUtil;
import services.CommunicationService;
import services.Factory;

import java.util.ArrayList;
import java.util.Arrays;

public class AuthOn implements ClientAction {
    private Client client;
    private CommunicationService communicationService;

    private final ArrayList<ResultCodes> LIST_EXPECT_RESULTCODE;

    public AuthOn(Client client) {
        this.client = client;
        this.communicationService = Factory.getCommunicationService();

        LIST_EXPECT_RESULTCODE = new ArrayList<>(Arrays.asList(ResultCodes.ERR_LOGIN, ResultCodes.ERR_PASSWORD, ResultCodes.OK));
    }

    @Override
    public Mess action() {
        Mess messResp = Factory.getCommunicationService().sendRemote(client.getMess());

        if (LIST_EXPECT_RESULTCODE.contains(messResp.getCode()))
            client.setIsAuth(true);
        else
            messResp = MessUtil.getRespErr(ResultCodes.ERR);

        return messResp;
    }
}
