package main;

import filesystem.*;
import message.*;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import dictionary.CommandCodes;
import dictionary.SelectTypes;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private final int PORT;
    private final String PATH_START = "C:\\temp\\";
    private final int BUF_SIZE = 20_000_000;

    private Dir dir;
    private File fil;
    private byte[] arrByte;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BufferedInputStream bisf;
    private BufferedOutputStream bosf;

    private boolean flgAuth = false;

    private Mess mess;
    private Mess messResp;
    private ResultCodes code;
    private CommandCodes command;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.PORT = socket.getPort();

        openConn();

        new Thread(() -> work()).start();
    }
    private void work() {
        while (!socket.isClosed()) {
            try {
                mess = Mess.fromJson(dis.readUTF());
                System.out.println(PORT + " > " + mess.getType() + " " + (mess.getCommand() != null ? mess.getCommand() : ""));

                messResp = processMess(mess);

                System.out.println(PORT + " < " + messResp.getType() + " " + messResp.getCode());
                dos.writeUTF(messResp.toJson());

                if (messResp.getType() == MessageTypes.CONN_CLOSE_RESP && messResp.getCode() == ResultCodes.OK) closeConn();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            case CONN_CLOSE : return close();
            case DIR_SET    : return routeMess(mess);
            case DIR_INFO   : return routeMess(mess);
            case DIR_ADD    : return routeMess(mess);
            case DIR_DEL    : return routeMess(mess);
            case DIR_COPY   : return filCopy(mess);
            default : return MessUtil.getErr(ResultCodes.ERR_MESS);
        }
    }
    private Mess authOn(Mess mess) {
        try {
            code = Server.authLoginPassword(mess.getLogin(), mess.getPassword());
            flgAuth = (code == ResultCodes.OK);

            if (flgAuth) dir = new Dir(PATH_START + mess.getLogin(), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return MessUtil.getResp(mess, code);
    }
    private Mess authOff(Mess mess) {
        flgAuth = false;
        return MessUtil.getRespOk(mess);
    }
    private Mess close() {
        return MessUtil.getRespOk(mess);
    }
    private Mess routeMess(Mess mess) {
        return dir.work(mess);
    }
    private Mess filCopy(Mess mess) {
        if (mess.isFlgServer())
            return sendFile(mess);
        else
            return receiveFile(mess);
    }
    private Mess receiveFile(Mess mess) {
        messResp = setServerFile(mess);
        if (messResp.getCode() != ResultCodes.OK)
            return MessUtil.getResp(mess, code);
        else
            sendIO(messResp);

        while (true) {
            messResp = receiveFilePortion();
            if (messResp.getCommand() == CommandCodes.RECEIVE) {
                saveFilePortion();
                sendIO(messResp);
            } else
                break;
        }

        resetFile(true);
        return messResp;
    }
    private Mess sendFile(Mess mess) {
        messResp = setServerFile_(mess);
        if (messResp.getCode() != ResultCodes.OK)
            return MessUtil.getResp(mess, code);
        else
            sendIO(messResp);

        while (true) {
            readFilePortion();
            if (arrByte.length > 0 ) {
                messResp = sendFilePortion();
                if (messResp.getCode() != ResultCodes.OK)
                    return MessUtil.getResp(mess, code);
            } else
                break;
        }

        messResp = filCopyCompl();
        resetFile(false);
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
    private Mess setServerFile(Mess mess) {
        try {
            messResp = dir.work(mess);

            if (messResp.getCode() != ResultCodes.OK) return MessUtil.getResp(mess, ResultCodes.NO_FILE_SELECTED);

            fil = new File(messResp.getDirPath() + "\\" + messResp.getSelectName());
            bosf = new BufferedOutputStream(new FileOutputStream(fil), BUF_SIZE);

            return MessUtil.getRespOk(mess);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MessUtil.getErr(ResultCodes.ERR);
    }
    private Mess setServerFile_(Mess mess) {
        try {
            messResp = dir.work(new Mess(MessageTypes.DIR_INFO));

            if (messResp.getSelectType() == SelectTypes.FIL) {
                mess.setDirPath(messResp.getDirPath());
                mess.setSelectName(messResp.getSelectName());
            } else
                return MessUtil.getResp(mess, ResultCodes.NO_FILE_SELECTED);

            fil = new File(mess.getDirPath() + "\\" + mess.getSelectName());
            bisf = new BufferedInputStream(new FileInputStream(fil), BUF_SIZE);

            return MessUtil.getRespOk(mess);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return MessUtil.getErr(ResultCodes.ERR);
    }
    private Mess receiveFilePortion() {
        try {
            mess = receiveIO();

            if (mess.getType() == MessageTypes.DIR_COPY) {
                switch (mess.getCommand()) {
                    case RECEIVE :
                        arrByte = new byte[mess.getValInt()];
                        dis.readFully(arrByte);

                        System.out.println(PORT + " >> " + "получено: " + arrByte.length);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Mess sendFilePortion() {
        try {
            mess = receiveIO();

            if (mess.getType() == MessageTypes.DIR_COPY) {
                switch (mess.getCommand()) {
                    case SEND :
                        messResp = MessUtil.getRespOk(mess);
                        messResp.setValInt(arrByte.length);
                        sendIO(messResp);

                        dos.write(arrByte);
                        dos.flush();

                        System.out.println(PORT + " << " + "передано: " + arrByte.length);
                        break;

                    default :
                        messResp = MessUtil.getErr(ResultCodes.ERR_MESS);
                        break;
                }
            } else {
                messResp = MessUtil.getErr(ResultCodes.ERR_MESS);
            }
            return messResp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Mess filCopyCompl() {
        Mess mess = receiveIO();

        messResp = new Mess(MessageTypes.DIR_COPY_RESP);

        return messResp;
    }

    private void sendIO(Mess mess) {
        try {
            System.out.println(PORT + " << " + mess.getType() + " " + mess.getCode());
            dos.writeUTF(mess.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Mess receiveIO() {
        try {
            mess = Mess.fromJson(dis.readUTF());
            System.out.println(PORT + " >> " + mess.getType() + " " + mess.getCommand());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mess;
    }
    private void openConn() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            System.out.println(PORT + " Клиент подключился");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void closeConn() {
        try {
//            bos.close();
//            bis.close();
            dos.close();
            dis.close();
            socket.close();

            System.out.println(PORT + " Клиент отключился");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
