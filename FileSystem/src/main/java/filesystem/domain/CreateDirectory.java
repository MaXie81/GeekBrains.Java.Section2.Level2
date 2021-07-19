package filesystem.domain;

import dictionary.MessageTypes;
import dictionary.ResultCodes;
import dictionary.SelectTypes;
import filesystem.main.Directory;
import message.Mess;
import message.MessUtil;

import java.io.File;

public class CreateDirectory implements DirectoryAction {
    private Directory directory;

    public CreateDirectory(Directory directory) {
        this.directory = directory;
    }

    @Override
    public Mess action(Mess mess) {
        String dirName = mess.getSelectName();

        directory.setSelectType(directory.getType(dirName));
        if (directory.getSelectType() != SelectTypes.NOT_EXIST) return MessUtil.getResp(mess, ResultCodes.ERR);

        String dirPath = directory.getDirectory().getPath() + "\\" + dirName;
        new File(dirPath).mkdir();

        ResultCodes code = directory.setPosition(dirName, true);

        Mess messResp = new Mess(MessageTypes.ADD_DIRECTORY_RESP);
        messResp.setCode(code);

        return messResp;
    }
}
