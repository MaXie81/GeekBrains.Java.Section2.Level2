package filesystem.domain;

import dictionary.MessageTypes;
import dictionary.ResultCodes;
import dictionary.SelectTypes;
import filesystem.main.Directory;
import message.Mess;
import message.MessUtil;

import java.io.File;

public class Delete implements DirectoryAction {
    private Directory directory;

    public Delete(Directory directory) {
        this.directory = directory;
    }

    @Override
    public Mess action(Mess mess) {
        directory.setSelectType(directory.getType(directory.getSelect()));
        if (!(directory.getSelectType() == SelectTypes.DIR || directory.getSelectType() == SelectTypes.FIL)) return MessUtil.getResp(mess, ResultCodes.ERR);

        directory.deleteDir(new File(directory.getDirectory(), directory.getSelect()));

        ResultCodes code = directory.setPosition(".", false);

        Mess messResp = new Mess(MessageTypes.DELETE_RESP);
        messResp.setCode(code);

        return messResp;
    }
}
