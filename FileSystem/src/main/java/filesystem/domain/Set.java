package filesystem.domain;

import dictionary.MessageTypes;
import dictionary.ResultCodes;
import filesystem.main.Directory;
import message.Mess;

public class Set implements DirectoryAction {
    private Directory directory;

    public Set(Directory directory) {
        this.directory = directory;
    }

    @Override
    public Mess action(Mess mess) {
        ResultCodes code = directory.setPosition(mess.getSelectName(), mess.getCntClick() == 2);

        Mess messResp = directory.getMapDirectoryAction().get(MessageTypes.GET_DIRECTORY).action(mess);

        messResp = new Mess(MessageTypes.SET_DIRECTORY_RESP);
        messResp.setCode(code);

        return messResp;
    }
}
