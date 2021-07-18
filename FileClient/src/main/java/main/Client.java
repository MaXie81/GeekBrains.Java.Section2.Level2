package main;

import domain.ClientAction;
import message.*;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import services.Communication;
import services.Factory;

import java.util.HashMap;

public class Client {
    private boolean isAuth = false;

    private Communication communication;
    private HashMap<MessageTypes, ClientAction> mapClientAction;

    public Client() {
        communication = new Communication();
        mapClientAction = Factory.getMapClientAction(this);
    }
    public Mess work(Mess mess) {
        if (!communication.isConnection()) communication.openConnection();
        if (!isAuth)
            if (!(mess.getType() == MessageTypes.AUTH_ON || mess.getType() == MessageTypes.CONN_CLOSE))
                return MessUtil.getErr(ResultCodes.ERR_MESS);
        if (isAuth)
            if (mess.getType() == MessageTypes.AUTH_ON)
                return MessUtil.getErr(ResultCodes.ERR_MESS);

        return mapClientAction.get(mess.getType()).action(mess);
    }
    public void setIsAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }
    public boolean isAuth() {
        return isAuth;
    }
    public Communication getCommunication() {
        return communication;
    }
}
