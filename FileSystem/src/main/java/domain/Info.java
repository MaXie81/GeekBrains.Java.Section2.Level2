package domain;

import dictionary.MessageTypes;
import filesystem.Directory;
import message.Mess;

public class Info implements DirectoryAction {
    Directory directory;

    public Info(Directory directory) {
        this.directory = directory;
    }

    @Override
    public Mess action(Mess mess) {
        Mess messResp = new Mess(MessageTypes.DIR_INFO_RESP);
        messResp.setDirName(directory.getDirectory().getName());
        messResp.setDirPath(directory.getDirectory().getPath());
        messResp.setFlgDirRoot(directory.isRootDirectory());
        messResp.setSelectName(directory.getSelect());
        messResp.setSelectType(directory.getSelectType());
        messResp.setListFile(directory.getDirectoryList());

        return messResp;
    }
}
