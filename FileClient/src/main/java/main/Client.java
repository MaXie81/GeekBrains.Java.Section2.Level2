package main;

import filesystem.*;
import message.*;
import factory.*;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import dictionary.CommandTypes;
import dictionary.SelectTypes;
import services.CommunicationService;
import services.Factory;

import java.io.*;
import java.util.Properties;

public class Client {
    private final int BUF_SIZE;

    private Dir dir;
    private File fil;
    private byte[] arrByte;

    private BufferedInputStream bisf;
    private BufferedOutputStream bosf;

    private boolean flgAuth = false;

    private Mess mess;
    private Mess messResp;
    private ResultCodes code;

    private CommunicationService communicationService;

    public Client() {
        Properties properties = PropertiesFactory.getProperties(true);

        BUF_SIZE = Integer.parseInt(properties.getProperty("BUF_SIZE").replaceAll("\\D", ""));

        dir = Factory.getDir().getDir();
        communicationService = Factory.getCommunicationService(dir);
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
        messResp = communicationService.sendSrv(mess);

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
        messResp = communicationService.sendSrv(mess);

        if (MessUtil.isRespOK(mess, messResp)) {
            flgAuth = false;
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR);
        }
        return messResp;
    }
    private Mess disconn(Mess mess) {
        messResp = communicationService.sendSrv(mess);

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
            return receiveFile(mess);
        else
            return sendFile(mess);
    }
    private Mess sendFile(Mess mess) {
        messResp = setClientFileForSend(mess);
        if (!MessUtil.isRespOK(mess, messResp)) return abortCopyFile(messResp);

        messResp = setServerFileForSend(mess);
        if (!MessUtil.isRespOK(mess, messResp)) return abortCopyFile(messResp);

        while (true) {
            readFilePortion();
            if (arrByte.length > 0 ) {
                messResp = sendFilePortion(mess);
                if (!MessUtil.isRespOK(mess, messResp)) return abortCopyFile(messResp);
            } else {
                messResp = completeFileCopy(mess);
                if (!MessUtil.isRespOK(mess, messResp)) return abortCopyFile(messResp);
                break;
            }
        }

        resetFile(true);
        return messResp;
    }
    private Mess receiveFile(Mess mess) {
        messResp = setServerFileForReceive(mess);
        if (!MessUtil.isRespOK(mess, messResp)) return abortCopyFile(messResp);

        messResp = setClientFileForReceive(mess);
        if (!MessUtil.isRespOK(mess, messResp)) return abortCopyFile(messResp);

        while (true) {
            messResp = receiveFilePortion(mess);
            if (!MessUtil.isRespOK(mess, messResp)) return abortCopyFile(messResp);
            if (messResp.getCommand() == CommandTypes.SEND) {
                saveFilePortion();
            } else
                break;
        }

        resetFile(false);
        return messResp;
    }
    private Mess abortCopyFile(Mess messResp) {
        try {
            if (bisf != null) bisf.close();
            if (bosf != null) bosf.close();
            if (fil != null) fil = null;

            return messResp;
        } catch (IOException e) {
            return MessUtil.getRespErr(ResultCodes.ERR);
        }
    }
    private void readFilePortion() {
        try {
            int size = bisf.available() < BUF_SIZE ? bisf.available() : BUF_SIZE;
            arrByte = new byte[size];
            bisf.read(arrByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveFilePortion() {
        try {
            bosf.write(arrByte);
            bosf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void resetFile(boolean flgCopyFromClientToServer) {
        if (flgCopyFromClientToServer) {
            try {
                bisf.close();
                fil = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                bosf.close();
                fil = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Mess setClientFileForSend(Mess mess) {
        try {
            messResp = work(new Mess(MessageTypes.DIR_INFO));

            if (messResp.getSelectType() == SelectTypes.FIL) {
                mess.setDirPath(messResp.getDirPath());
                mess.setSelectName(messResp.getSelectName());
            } else
                return MessUtil.getResp(mess, ResultCodes.NO_FILE_SELECTED);

            fil = new File(mess.getDirPath() + "\\" + mess.getSelectName());
            bisf = new BufferedInputStream(new FileInputStream(fil), BUF_SIZE);

            return MessUtil.getRespOk(mess);
        } catch (IOException e) {
            fil = null;
            return MessUtil.getRespErr(ResultCodes.ERR);
        }
    }
    private Mess setServerFileForSend(Mess mess) {
        mess.setCommand(CommandTypes.SET);
        messResp = communicationService.sendSrv(mess);

        return messResp;
    }
    private Mess setServerFileForReceive(Mess mess) {
        mess.setCommand(CommandTypes.SET);
        messResp = communicationService.sendSrv(mess);

        if (MessUtil.isRespOK(mess, messResp)) {
            mess.setDirPath(messResp.getDirPath());
            mess.setSelectName(messResp.getSelectName());
            return messResp;
        } else
            return MessUtil.getResp(mess, ResultCodes.NO_FILE_SELECTED);
    }
    private Mess setClientFileForReceive(Mess mess) {
        try {
            messResp = dir.work(mess);

            fil = new File(messResp.getDirPath() + "\\" + messResp.getSelectName());
            bosf = new BufferedOutputStream(new FileOutputStream(fil), BUF_SIZE);

            return MessUtil.getRespOk(mess);
        } catch (IOException e) {
            fil = null;
            return MessUtil.getRespErr(ResultCodes.ERR);
        }
    }
    private Mess sendFilePortion(Mess mess) {
        mess.setCommand(CommandTypes.RECEIVE);
        mess.setValInt(arrByte.length);

        communicationService.sendIO(mess);
        communicationService.sendFilePortion(arrByte);

        return communicationService.receiveIO();
    }
    private Mess receiveFilePortion(Mess mess) {
        mess.setCommand(CommandTypes.SEND);
        messResp = communicationService.sendSrv(mess);

        arrByte = new byte[messResp.getValInt()];
        arrByte = communicationService.receiveFilePortion(arrByte.length);

        return messResp;
    }
    private Mess completeFileCopy(Mess mess) {
        mess.setCommand(CommandTypes.COMPLITE);
        mess.setValLong(fil.length());
        return communicationService.sendSrv(mess);
    }
    public boolean isFlgAuth() {
        return flgAuth;
    }
}
