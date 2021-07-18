package filesystem;

import dictionary.MessageTypes;
import domain.*;
import message.Mess;
import dictionary.ResultCodes;
import dictionary.SelectTypes;
import services.Factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Directory {
    private final String START_PATH;

    private HashMap<MessageTypes, DirectoryAction> mapDirectoryAction;

    private File directory = null;
    private Boolean isRootDirectory;
    private String select = null;
    private SelectTypes selectType;
    private LinkedList<String> directoryList;

    public Directory(String dirPath, boolean flgStartPath)  {
        mapDirectoryAction = Factory.getMapDirectoryAction(this);

        File dirTest = new File(dirPath);
        if (!dirTest.exists())
            try {
                throw new FileNotFoundException("Указанной директории не существует!");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        START_PATH = flgStartPath ? dirPath : "";
        reset(dirTest, "*");
    }
    public File getDirectory() { return directory; }
    public Boolean isRootDirectory() { return isRootDirectory; }
    public String getSelect() { return select; }
    public SelectTypes getSelectType() { return selectType; }
    public void setSelectType(SelectTypes selectType) { this.selectType = selectType; }
    public LinkedList<String> getDirectoryList() { return directoryList; }

    public HashMap<MessageTypes, DirectoryAction> getMapDirectoryAction() {
        return mapDirectoryAction;
    }

    public Mess work(Mess mess) { return mapDirectoryAction.get(mess.getType()).action(mess); }
    public SelectTypes getType(String name) {
        if (name == null) return SelectTypes.INVALID_NAME;
        if (name.equals("")) return SelectTypes.INVALID_NAME;
        if (name.equals("*")) return SelectTypes.ALL;
        if (name.equals(".")) return SelectTypes.DIR_CURR;
        if (name.equals("..")) return SelectTypes.DIR_PREV;

        File nameTest = new File(directory.getPath() + "\\" + name);
        if (!nameTest.exists())
            return SelectTypes.NOT_EXIST;
        if (nameTest.isDirectory())
            return SelectTypes.DIR;
        else
            return SelectTypes.FIL;
    }
    public ResultCodes setPosition(String name, boolean flgMove) {
        switch (getType(name)) {
            case INVALID_NAME : return ResultCodes.INVALID_NAME;
            case ALL: return ResultCodes.INVALID_NAME;
            case NOT_EXIST: return ResultCodes.FILE_NOT_EXIST;
            case DIR_PREV :
                if (flgMove)
                    reset(directory.getParentFile(), "*");
                else
                    reset(directory, "*");
                break;
            case DIR_CURR :
                reset(directory, "*");
                break;
            case DIR :
                if (flgMove) {
                    File nameDir = new File(directory.getPath() + "\\" + name);
                    reset(nameDir, "*");
                    break;
                } else {
                    reset(directory, name);
                    break;
                }
            case FIL :
                reset(directory, name);
                break;
            default : return ResultCodes.ERR;
        }
        return ResultCodes.OK;
    }
    private void reset(File chgDir, String chgSelect) {
        if (START_PATH.equals(""))
            isRootDirectory = (chgDir.getParentFile() == null);
        else
            isRootDirectory = START_PATH.equals(chgDir.getPath());

        directory = chgDir;

        if (select != chgSelect) {
            select = chgSelect;
            selectType = getType(chgSelect);
        }

        directoryList = new LinkedList<>();
        if (!isRootDirectory) directoryList.add("..");
        directoryList.addAll(Arrays.asList(directory.list()));
    }
    public void deleteDir(File dir) {
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

