package message;

import dictionary.MessageTypes;
import dictionary.ResultCodes;

import java.util.HashMap;

public class MessUtil {
    private static HashMap<MessageTypes, MessageTypes> mapMessResp = new HashMap<>();
    static {
        mapMessResp.put(MessageTypes.AUTH_ON, MessageTypes.AUTH_ON_RESP);
        mapMessResp.put(MessageTypes.AUTH_OFF, MessageTypes.AUTH_OFF_RESP);
        mapMessResp.put(MessageTypes.CLOSE_CONNECTION, MessageTypes.CLOSE_CONNECTION_RESP);
        mapMessResp.put(MessageTypes.SET_DIRECTORY, MessageTypes.SET_DIRECTORY_RESP);
        mapMessResp.put(MessageTypes.GET_DIRECTORY, MessageTypes.GET_DIRECTORY_RESP);
        mapMessResp.put(MessageTypes.COPY_FILE, MessageTypes.COPY_FILE_RESP);
        mapMessResp.put(MessageTypes.ADD_DIRECTORY, MessageTypes.ADD_DIRECTORY_RESP);
        mapMessResp.put(MessageTypes.DELETE, MessageTypes.DELETE_RESP);

    }
    public static boolean isRespOK(Mess mess, Mess messResp) {
        if (messResp.getType() != mapMessResp.get(mess.getType())) return false;
        if (messResp.getCommand() != mess.getCommand()) return false;
        if (messResp.getCode() != ResultCodes.OK) return false;
        return true;
    }
    public static Mess getErr(ResultCodes code) {
        Mess mess = new Mess(MessageTypes.ERR);
        mess.setCode(code);
        return mess;
    }
    public static Mess getRespErr(ResultCodes code) {
        Mess messResp = new Mess(MessageTypes.ERR_RESP);
        messResp.setCode(code);
        return messResp;
    }
    public static Mess getResp(Mess mess, ResultCodes code) {
        Mess messResp = Mess.fromJson(mess.toJson());
        messResp.setType(mapMessResp.get(mess.getType()));
        messResp.setCode(code);

        return messResp;
    }
    public static Mess getRespOk(Mess mess) {
        return getResp(mess, ResultCodes.OK);
    }
}
