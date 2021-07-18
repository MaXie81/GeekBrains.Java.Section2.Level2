package domain;

import dictionary.MessageTypes;
import dictionary.ResultCodes;
import filesystem.Directory;
import message.Mess;

public class Set implements DirectoryAction {
    private Directory directory;

    public Set(Directory directory) {
        this.directory = directory;
    }

    @Override
    public Mess action(Mess mess) {
        ResultCodes code = directory.setPosition(mess.getSelectName(), mess.getCntClick() == 2);

        Mess messResp = new Info(directory).action(mess);

        messResp = new Mess(MessageTypes.DIR_SET_RESP);
        messResp.setCode(code);

        return messResp;
    }
}
