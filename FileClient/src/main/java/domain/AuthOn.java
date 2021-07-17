package domain;

import dictionary.ResultCodes;
import main.Client;
import message.Mess;
import message.MessUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class AuthOn implements ClientAction {
    private Client client;

    private final ArrayList<ResultCodes> LIST_EXPECT_RESULTCODE;

    public AuthOn(Client client) {
        this.client = client;

        LIST_EXPECT_RESULTCODE = new ArrayList<>(Arrays.asList(ResultCodes.ERR_LOGIN, ResultCodes.ERR_PASSWORD, ResultCodes.OK));
    }

    @Override
    public Mess action() {
        Mess messResp = client.getCommunication().sendRemote(client.getMess());

        if (!LIST_EXPECT_RESULTCODE.contains(messResp.getCode()))
            messResp = MessUtil.getRespErr(ResultCodes.ERR);

        if (messResp.getCode() == ResultCodes.OK)
            client.setIsAuth(true);

        return messResp;
    }
}
