package server.main;

import server.domain.*;
import server.services.Communication;
import server.services.Factory;
import message.*;
import dictionary.MessageTypes;
import dictionary.ResultCodes;

import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;

public class ClientHandler {
    private final int PORT;

    private boolean isAuth = false;

    private Mess mess;
    private Mess messResp;

    private Communication communication;
    private HashMap<MessageTypes, ServerAction> mapClientAction;

    public ClientHandler(Socket socket) {
        communication = new Communication(socket);
        mapClientAction = Factory.getMapClientAction(this);

        this.PORT = socket.getPort();

        Properties properties = Factory.getProperties();

        new Thread(() -> work()).start();
    }
    private void work() {
        if (!communication.isConnection()) communication.openConnection();

        while (communication.isConnection()) {
            mess = communication.receive();
            messResp = processMess(mess);
            communication.sendRemote(messResp);

            if (messResp.getType() == MessageTypes.CLOSE_CONNECTION_RESP && messResp.getCode() == ResultCodes.OK) communication.closeConnection();
        }
    }
    private Mess processMess(Mess mess) {
        if (!isAuth)
            if (!(mess.getType() == MessageTypes.AUTH_ON || mess.getType() == MessageTypes.CLOSE_CONNECTION))
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
    public HashMap<MessageTypes, ServerAction> getMapClientAction() { return mapClientAction; }
}
