package filesystem;

import message.Mess;
import message.MessUtil;
import dictionary.MessageTypes;
import dictionary.ResultCodes;
import dictionary.SelectTypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class Dir {
    private final String START_PATH;
    private final String COPY_PREFIX = "copy(x)_";
    
    private File dir = null;
    private Boolean flgRoot;
    private String select = null;
    private SelectTypes selectType;
    private LinkedList<String> dirList;

    private Mess messResp;
    private ResultCodes code;
    private SelectTypes dictionary;

    public Dir(String dirPath, boolean flgStartPath) throws FileNotFoundException {
        File dirTest = new File(dirPath);
        if (!dirTest.exists())
            throw new FileNotFoundException("Указанной директории не существует!");

        START_PATH = flgStartPath ? dirPath : "";
        reset(dirTest, "*");
    }
    public Mess work(Mess mess) {
        switch (mess.getType()) {
            case DIR_INFO : return info();
            case DIR_SET  : return set(mess);
            case DIR_ADD  : return createDir(mess);
            case DIR_DEL  : return delete(mess);
            case DIR_COPY : return addFil(mess);

            default : throw new IllegalStateException("Unexpected value: " + mess.getType());
        }
    }
    private Mess info() {
        messResp = new Mess(MessageTypes.DIR_INFO_RESP);
        messResp.setDirName(dir.getName());
        messResp.setDirPath(dir.getPath());
        messResp.setFlgDirRoot(flgRoot);
        messResp.setSelectName(select);
        messResp.setSelectType(selectType);
        messResp.setListFile(dirList);

        return messResp;
    }
    private Mess set(Mess mess) {
        code = setPosition(mess.getSelectName(), mess.getCntClick() == 2);

        messResp = info();

        messResp = new Mess(MessageTypes.DIR_SET_RESP);
        messResp.setCode(code);

        return messResp;
    }
    private Mess createDir(Mess mess) {
        String dirName = mess.getSelectName();

        selectType = getType(dirName);
        if (selectType != SelectTypes.NOT_EXIST) return MessUtil.getResp(mess, ResultCodes.ERR);

        String dirPath = dir.getPath() + "\\" + dirName;
        new File(dirPath).mkdir();

        code = setPosition(dirName, true);

        messResp = new Mess(MessageTypes.DIR_ADD_RESP);
        messResp.setCode(code);

        return messResp;
    }
    private Mess delete(Mess mess) {
        selectType = getType(select);
        if (!(selectType == SelectTypes.DIR || selectType == SelectTypes.FIL)) return MessUtil.getResp(mess, ResultCodes.ERR);

        deleteDir(new File(dir, select));

        code = setPosition(".", false);

        messResp = new Mess(MessageTypes.DIR_DEL_RESP);
        messResp.setCode(code);

        return messResp;
    }
    private Mess addFil(Mess mess) {
        String name = mess.getSelectName();
        String filName = name;
        int cnt = 0;

        try {
            while (dirList.contains(filName)) {
                filName = COPY_PREFIX.replaceAll("x", String.valueOf(++cnt)) + name;
            }

            File fil = new File(dir.getPath() + "\\" + filName);
            fil.createNewFile();

            code = setPosition(fil.getName(), false);

            messResp = MessUtil.getRespOk(mess);
            messResp.setDirPath(dir.getPath());
            messResp.setSelectName(select);
            messResp.setCode(code);
            return messResp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MessUtil.getErr(ResultCodes.ERR);
    }
    private SelectTypes getType(String name) {
        if (name == null) return SelectTypes.INVALID_NAME;
        if (name.equals("")) return SelectTypes.INVALID_NAME;
        if (name.equals("*")) return SelectTypes.DIR_ALL;
        if (name.equals(".")) return SelectTypes.DIR_CURR;
        if (name.equals("..")) return SelectTypes.DIR_PREV;

        File nameTest = new File(dir.getPath() + "\\" + name);
        if (!nameTest.exists())
            return SelectTypes.NOT_EXIST;
        if (nameTest.isDirectory())
            return SelectTypes.DIR;
        else
            return SelectTypes.FIL;
    }
    private ResultCodes setPosition(String name, boolean flgMove) {
        switch (getType(name)) {
            case INVALID_NAME : return ResultCodes.INVALID_NAME;
            case DIR_ALL : return ResultCodes.INVALID_NAME;
            case NOT_EXIST: return ResultCodes.FILE_NOT_EXIST;
            case DIR_PREV :
                if (flgMove)
                    reset(dir.getParentFile(), "*");
                else
                    reset(dir, "*");
                break;
            case DIR_CURR :
                reset(dir, "*");
                break;
            case DIR :
                if (flgMove) {
                    File nameDir = new File(dir.getPath() + "\\" + name);
                    reset(nameDir, "*");
                    break;
                } else {
                    reset(dir, name);
                    break;
                }
            case FIL :
                reset(dir, name);
                break;
            default : return ResultCodes.ERR;
        }
        return ResultCodes.OK;
    }
    private void reset(File chgDir, String chgSelect) {
        if (START_PATH.equals(""))
            flgRoot = (chgDir.getParentFile() == null);
        else
            flgRoot = START_PATH.equals(chgDir.getPath());

        dir = chgDir;

        if (select != chgSelect) {
            select = chgSelect;
            selectType = getType(chgSelect);
        }

        dirList = new LinkedList<>();
        if (!flgRoot) dirList.add("..");
        dirList.addAll(Arrays.asList(dir.list()));
    }
    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDir(f);
            }
            dir.delete();
        } else dir.delete();
    }
}

