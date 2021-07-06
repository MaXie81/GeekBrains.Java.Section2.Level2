package message;

import com.google.gson.Gson;
import dictionary.ResultCodes;
import dictionary.CommandTypes;
import dictionary.SelectTypes;
import dictionary.MessageTypes;
import lombok.Data;

import java.util.LinkedList;
@Data
public class Mess {
    private MessageTypes type;
    private CommandTypes command = CommandTypes.NOT_DEFINED;
    private boolean flgServer = false;

    private String dirName;
    private String dirPath;
    private boolean flgDirRoot;
    private String selectName;
    private SelectTypes selectType;
    private LinkedList<String> listFile;

    private String login;
    private String password;

    private int cntClick;
    private ResultCodes code = ResultCodes.OK;

    private String valSting = "";
    private int valInt;
    private long valLong;
    private boolean flg;

    public Mess(MessageTypes type) {
        this.type = type;
    }
    public static Mess fromJson(String json) {
        return new Gson().fromJson(json, Mess.class);
    }
    public String toJson() {
        return new Gson().toJson(this);
    }
}
