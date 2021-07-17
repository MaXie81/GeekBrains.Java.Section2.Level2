package main;

import message.*;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import services.CommunicationService;
import services.Factory;

import java.io.*;
import java.util.Properties;

public class Client {
    private final int BUF_SIZE;

    private File fil;
    private byte[] arrByte;

    private BufferedInputStream bisf;
    private BufferedOutputStream bosf;

    private boolean flgAuth = false;

    private Mess mess;
    private Mess messResp;
    private ResultCodes code;

    private CommunicationService communicationService;
    private FileCopy fileCopy;

    public Client() {
        Properties properties = Factory.getProperties();
        BUF_SIZE = Integer.parseInt(properties.getProperty("BUF_SIZE").replaceAll("\\D", ""));

        communicationService = Factory.getCommunicationService();
        fileCopy = new FileCopy(communicationService);
    }
    public Mess work(Mess mess) {
        if (!communicationService.isConnection()) communicationService.openConn();
        return processMess(mess);
    }
    private Mess processMess(Mess mess) {
        if (!flgAuth)
            if (!(mess.getType() == MessageTypes.AUTH_ON || mess.getType() == MessageTypes.CONN_CLOSE))
                return MessUtil.getErr(ResultCodes.ERR_MESS);

        if (flgAuth)
            if (mess.getType() == MessageTypes.AUTH_ON)
                return MessUtil.getErr(ResultCodes.ERR_MESS);

        switch (mess.getType()) {
            case AUTH_ON    : return authOn(mess);
            case AUTH_OFF   : return authOff(mess);
            case CONN_CLOSE : return disconn(mess);
            case DIR_INFO   : return routeMess(mess);
            case DIR_SET    : return routeMess(mess);
            case FILE_ADD   : return routeMess(mess);
            case DIR_DEL    : return routeMess(mess);
            case DIR_COPY   : return сopyFile(mess);
            default : return MessUtil.getErr(ResultCodes.ERR_MESS);
        }
    }
    private Mess authOn(Mess mess) {
        messResp = communicationService.sendRemote(mess);

        switch (messResp.getCode()) {
            case ERR_LOGIN :
                break;
            case ERR_PASSWORD :
                break;
            case OK :
                flgAuth = true;
                break;
            default :
                messResp = MessUtil.getErr(ResultCodes.ERR);
                break;
        }
        return messResp;
    }
    private Mess authOff(Mess mess) {
        messResp = communicationService.sendRemote(mess);

        if (MessUtil.isRespOK(mess, messResp)) {
            flgAuth = false;
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR);
        }
        return messResp;
    }
    private Mess disconn(Mess mess) {
        messResp = communicationService.sendRemote(mess);

        if (MessUtil.isRespOK(mess, messResp)) {
            flgAuth = false;
            communicationService.closeConn();
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR);
        }
        return messResp;
    }
    private Mess routeMess(Mess mess) {
        return communicationService.send(mess);
    }
    private Mess сopyFile(Mess mess) {
        if (mess.isFlgServer())
            return fileCopy.receiveFile(mess);
        else
            return fileCopy.sendFile(mess);
    }
    public boolean isFlgAuth() {
        return flgAuth;
    }
}
