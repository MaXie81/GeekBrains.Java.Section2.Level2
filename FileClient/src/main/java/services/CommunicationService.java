package services;

import dictionary.CommandTypes;
import factory.PropertiesFactory;
import filesystem.Dir;
import message.Mess;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class CommunicationService {
    private final String HOST;
    private final int PORT;
//    private final String PATH_START;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private Dir dir;
    private boolean isConnection = false;

    private Mess mess;
    private Mess messResp;

    public CommunicationService(Dir dir) {
        Properties properties = PropertiesFactory.getProperties(true);

        HOST = properties.getProperty("HOST");
        PORT = Integer.parseInt(properties.getProperty("PORT"));
//        PATH_START = properties.getProperty("PATH_START");

        this.dir = dir;

//        try {
//            openConn();
//            dir = new Dir(PATH_START, false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    public Mess send(Mess mess) {
        if (mess.isFlgServer())
            return sendSrv(mess);
        else
            return dir.work(mess);
    }
    public Mess sendSrv(Mess mess) {
        sendIO(mess);
        return receiveIO();
    }
    public void sendIO(Mess mess) {
        try {
            System.out.println(PORT + " < " + mess.getType() + " " + (mess.getCommand() != CommandTypes.NOT_DEFINED ? mess.getCommand() : ""));
            dos.writeUTF(mess.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Mess receiveIO() {
        try {
            messResp = Mess.fromJson(dis.readUTF());
            System.out.println(PORT + " > " + messResp.getType() + " " + messResp.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messResp;
    }
    public void openConn() {
        try {
            socket = new Socket(HOST, PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            isConnection = true;

            System.out.println("Соединение с Сервером установлено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeConn() {
        try {
            isConnection = false;
            dos.close();
            dis.close();
            socket.close();

            System.out.println("Соединение с Сервером закрыто");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendFilePortion(byte[] arrByte) {
        try {
            dos.write(arrByte);
            dos.flush();

            System.out.println(PORT + " << " + "передано: " + arrByte.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] receiveFilePortion(int length) {
        try {
            byte[] arrByte = new byte[length];
            dis.readFully(arrByte);

            System.out.println(PORT + " >> " + "получено: " + arrByte.length);
            return arrByte;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean isConnection() {
        return isConnection;
    }
}
