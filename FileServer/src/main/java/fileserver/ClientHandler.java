package fileserver;

import domain.*;
import message.*;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import services.CommunicationServer;
import services.Factory_;

import java.net.Socket;
import java.util.Properties;

public class ClientHandler {
    private final int PORT;
    private final String START_PATH_START;

    private boolean isAuth = false;

    private Mess mess;
    private Mess messResp;

    private CommunicationServer communication;

    public ClientHandler(Socket socket) {
        communication = new CommunicationServer(socket);

        this.PORT = socket.getPort();

        Properties properties = Factory_.getProperties();
        START_PATH_START = properties.getProperty("START_PATH_START");

        new Thread(() -> work()).start();
    }
    private void work() {
        if (!communication.isConnection()) communication.openConnection();

        while (communication.isConnection()) {
            mess = communication.receive();
            messResp = processMess(mess);
            communication.sendRemote(messResp);

            if (messResp.getType() == MessageTypes.CONN_CLOSE_RESP && messResp.getCode() == ResultCodes.OK) communication.closeConnection();
        }
    }
    private Mess processMess(Mess mess) {
        if (!isAuth)
            if (!(mess.getType() == MessageTypes.AUTH_ON || mess.getType() == MessageTypes.CONN_CLOSE))
                return MessUtil.getErr(ResultCodes.ERR_MESS);

        if (isAuth)
            if (mess.getType() == MessageTypes.AUTH_ON)
                return MessUtil.getErr(ResultCodes.ERR_MESS);

        switch (mess.getType()) {
            case AUTH_ON    : return new AuthOn_(this).action(mess);
            case AUTH_OFF   : return new AuthOff_(this).action(mess);
            case CONN_CLOSE : return new Close_(this).action(mess);
            case DIR_SET    : return new RouteMess_(this).action(mess);
            case DIR_INFO   : return new RouteMess_(this).action(mess);
            case FILE_ADD   : return new RouteMess_(this).action(mess);
            case DIR_DEL    : return new RouteMess_(this).action(mess);
            case DIR_COPY   : return new CopyFile_(this).action(mess);
            default : return MessUtil.getErr(ResultCodes.ERR_MESS);
        }
    }

    public void setIsAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }
    public boolean isAuth() {
        return isAuth;
    }
    public CommunicationServer getCommunication() {
        return communication;
    }
}
