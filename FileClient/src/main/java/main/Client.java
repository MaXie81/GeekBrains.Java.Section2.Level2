package main;

import message.*;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import services.CommunicationService;
import services.Factory;

public class Client {
    private boolean isAuth = false;

    private CommunicationService communication;

    private Mess mess;
    private Mess messResp;
    private ResultCodes code;

    public Client() {
        communication = Factory.getCommunicationService();
    }
    public Mess getMess() {
        return mess;
    }
    public void setIsAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }
    public boolean isAuth() {
        return isAuth;
    }
    public CommunicationService getCommunication() {
        return communication;
    }
    public Mess work(Mess mess) {
        if (!communication.isConnection()) communication.openConnection();
        this.mess = mess;
        return processMess(mess);
    }
    private Mess processMess(Mess mess) {
        if (!isAuth)
            if (!(mess.getType() == MessageTypes.AUTH_ON || mess.getType() == MessageTypes.CONN_CLOSE))
                return MessUtil.getErr(ResultCodes.ERR_MESS);

        if (isAuth)
            if (mess.getType() == MessageTypes.AUTH_ON)
                return MessUtil.getErr(ResultCodes.ERR_MESS);

        return Factory.getMapClientAction(this).get(mess.getType()).action();
    }
}
