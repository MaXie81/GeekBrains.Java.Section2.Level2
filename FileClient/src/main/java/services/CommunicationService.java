package services;

import dictionary.CommandTypes;
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

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private Dir dir;
    private boolean isConnection = false;

    private Mess mess;
    private Mess messResp;

    public CommunicationService(Dir dir) {
        Properties properties = Factory.getProperties();
        HOST = properties.getProperty("HOST");
        PORT = Integer.parseInt(properties.getProperty("PORT"));

        this.dir = dir;
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
    public Mess send(Mess mess) {
        if (mess.isFlgServer())
            return sendRemote(mess);
        else
            return sendLocal(mess);
    }
    public Mess sendRemote(Mess mess) {
        sendIO(mess);
        return receiveIO();
    }
    public Mess sendLocal(Mess mess) {
        return dir.work(mess);
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
