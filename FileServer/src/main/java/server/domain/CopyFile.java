package server.domain;

import dictionary.CommandTypes;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import dictionary.SelectTypes;
import server.main.ClientHandler;
import message.Mess;
import message.MessUtil;
import server.services.Factory;

import java.io.*;
import java.util.Properties;

public class CopyFile implements ServerAction {
    private ClientHandler client;

    private final int BUF_SIZE;

    private File fil;
    private byte[] arrByte;

    private BufferedInputStream bisf;
    private BufferedOutputStream bosf;

    private Mess mess;
    private Mess messResp;
    private ResultCodes code;

    public CopyFile(ClientHandler client) {
        this.client = client;

        Properties properties = Factory.getProperties();
        BUF_SIZE = Integer.parseInt(properties.getProperty("BUF_SIZE").replaceAll("\\D", ""));
    }

    @Override
    public Mess action(Mess mess) {
        if (mess.isFlgServer())
            return sendFile(mess);
        else
            return receiveFile(mess);
    }
    private Mess sendFile(Mess mess) {
        messResp = setServerFileForSend(mess);
        if (!MessUtil.isRespOK(mess, messResp))
            return messResp;
        else
            client.getCommunication().sendIO(messResp);

        while (true) {
            readFilePortion();
            if (arrByte.length > 0 ) {
                messResp = sendFilePortion();
                if (messResp.getCode() != ResultCodes.OK) return MessUtil.getResp(mess, code);
            } else
                break;
        }

        messResp = filCopyCompl();
        resetFile(false);
        return messResp;
    }
    private Mess receiveFile(Mess mess) {
        messResp = setServerFileForReceive(mess);
        if (!MessUtil.isRespOK(mess, messResp))
            return messResp;
        else
            client.getCommunication().sendIO(messResp);
        while (true) {
            messResp = receiveFilePortion();
            if (messResp.getCommand() == CommandTypes.RECEIVE) {
                saveFilePortion();
                client.getCommunication().sendIO(messResp);
            } else
                break;
        }

        resetFile(true);
        return messResp;
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
    private void resetFile(boolean flgToServer) {
        if (!flgToServer) {
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

    private Mess setServerFileForReceive(Mess mess) {
        try {
            messResp = client.getCommunication().sendLocal(mess);

            if (!MessUtil.isRespOK(mess, messResp)) return MessUtil.getResp(mess, ResultCodes.NO_FILE_SELECTED);

            fil = new File(messResp.getDirPath() + "\\" + messResp.getSelectName());
            bosf = new BufferedOutputStream(new FileOutputStream(fil), BUF_SIZE);

            return MessUtil.getRespOk(mess);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MessUtil.getErr(ResultCodes.ERR);
    }
    private Mess setServerFileForSend(Mess mess) {
        try {
            messResp = client.getCommunication().sendLocal(new Mess(MessageTypes.DIR_INFO));

            if (messResp.getSelectType() == SelectTypes.FIL) {
                mess.setDirPath(messResp.getDirPath());
                mess.setSelectName(messResp.getSelectName());
            } else
                return MessUtil.getResp(mess, ResultCodes.NO_FILE_SELECTED);

            fil = new File(mess.getDirPath() + "\\" + mess.getSelectName());
            bisf = new BufferedInputStream(new FileInputStream(fil), BUF_SIZE);
            return MessUtil.getRespOk(mess);
        } catch (IOException e) {
            return MessUtil.getRespErr(ResultCodes.ERR);
        }
    }

    private Mess receiveFilePortion() {
        mess = client.getCommunication().receiveIO();
        if (mess.getType() == MessageTypes.DIR_COPY) {
            switch (mess.getCommand()) {
                case RECEIVE :
                    arrByte = new byte[mess.getValInt()];
                    arrByte = client.getCommunication().receiveFilePortion(arrByte.length);

                    messResp = MessUtil.getRespOk(mess);
                    messResp.setValInt(arrByte.length);
                    break;

                case COMPLITE :
                    messResp = MessUtil.getRespOk(mess);
                    messResp.setValLong(fil.length());
                    break;

                default :
                    messResp = MessUtil.getErr(ResultCodes.ERR_MESS);
                    break;
            }
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR_MESS);
        }
        return messResp;
    }
    private Mess sendFilePortion() {
        mess = client.getCommunication().receiveIO();

        if (mess.getType() == MessageTypes.DIR_COPY) {
            switch (mess.getCommand()) {
                case SEND :
                    messResp = MessUtil.getRespOk(mess);
                    messResp.setValInt(arrByte.length);

                    client.getCommunication().sendIO(messResp);
                    client.getCommunication().sendFilePortion(arrByte);

                    break;

                default :
                    messResp = MessUtil.getErr(ResultCodes.ERR_MESS);
                    break;
            }
        } else {
            messResp = MessUtil.getErr(ResultCodes.ERR_MESS);
        }
        return messResp;
    }

    private Mess filCopyCompl() {
        Mess mess = client.getCommunication().receiveIO();

        messResp = new Mess(MessageTypes.DIR_COPY_RESP);

        return messResp;
    }
}
