package filesystem.domain;

import dictionary.MessageTypes;
import filesystem.main.Directory;
import message.Mess;

public class GetInfo implements DirectoryAction {
    Directory directory;

    public GetInfo(Directory directory) {
        this.directory = directory;
    }

    @Override
    public Mess action(Mess mess) {
        Mess messResp = new Mess(MessageTypes.GET_DIRECTORY_RESP);
        messResp.setDirName(directory.getDirectory().getName());
        messResp.setDirPath(directory.getDirectory().getPath());
        messResp.setFlgDirRoot(directory.isRootDirectory());
        messResp.setSelectName(directory.getSelect());
        messResp.setSelectType(directory.getSelectType());
        messResp.setListFile(directory.getDirectoryList());

        return messResp;
    }
}
