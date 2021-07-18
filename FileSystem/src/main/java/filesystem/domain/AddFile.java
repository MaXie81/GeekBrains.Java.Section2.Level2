package filesystem.domain;

import dictionary.ResultCodes;
import filesystem.main.Directory;
import message.Mess;
import message.MessUtil;
import filesystem.services.Factory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class AddFile implements DirectoryAction {
    private final String COPY_PREFIX;
    private Directory directory;

    public AddFile(Directory directory) {
        Properties properties = Factory.getProperties();
        COPY_PREFIX = properties.getProperty("COPY_PREFIX");
        this.directory = directory;
    }

    @Override
    public Mess action(Mess mess) {
        String name = mess.getSelectName();
        String filName = name;
        int cnt = 0;

        try {
            while (directory.getDirectoryList().contains(filName)) {
                filName = COPY_PREFIX.replaceAll("x", String.valueOf(++cnt)) + name;
            }

            File fil = new File(directory.getDirectory().getPath() + "\\" + filName);
            fil.createNewFile();

            ResultCodes code = directory.setPosition(fil.getName(), false);

            Mess messResp = MessUtil.getRespOk(mess);
            messResp.setDirPath(directory.getDirectory().getPath());
            messResp.setSelectName(directory.getSelect());
            messResp.setCode(code);
            return messResp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MessUtil.getErr(ResultCodes.ERR);
    }
}
