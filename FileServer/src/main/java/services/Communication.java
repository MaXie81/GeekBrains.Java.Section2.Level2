package services;

import dictionary.CommandTypes;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import filesystem.Directory;
import message.Mess;
import message.MessUtil;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class Communication {
    private final int PORT;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private Directory directory;
    private boolean isConnection = false;

    private Mess mess;
    private Mess messResp;

    public Communication(Socket socket) {
        Properties properties = Factory.getProperties();

        this.socket = socket;
        PORT = socket.getPort();

        try {
            directory = new Directory(properties.getProperty("START_PATH_START"), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        return directory.work(mess);
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
            mess = Mess.fromJson(dis.readUTF());
            System.out.println(PORT + " > " + mess.getType() + " " + mess.getCommand());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mess;
    }
    public void openConnection() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            isConnection = true;

            System.out.println(PORT + " Клиент подключился");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeConnection() {
        try {
            isConnection = false;
            dos.close();
            dis.close();
            socket.close();

            System.out.println(PORT + " Клиент отключился");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
