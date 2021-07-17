package window;

import dictionary.ResultCodes;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.Client;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import message.Mess;
import dictionary.MessageTypes;

import javax.swing.*;

public class Controller {
    private Client client;

    private Mess mess;
    private Mess messResp;

    private boolean flgServer;

    @FXML
    public HBox panelAuth;
    @FXML
    public TextField txtLogin;
    @FXML
    public PasswordField pswPassword;
    @FXML
    public Button btnConnect;
    @FXML
    public TextField txtClientPath;
    @FXML
    public TextField txtServerPath;
    @FXML
    public HBox panelDir;
    @FXML
    public ListView lstClientDir;
    @FXML
    public ListView lstServerDir;
    @FXML
    public HBox panelComand;

    public void initialize() {
        client = new Client();
        refreshForm();
    }

    @FXML
    private void onMouseClickConnect(ActionEvent actionEvent) {
        auth();
    }
    @FXML
    private void onKeyEnterConnect(KeyEvent keyEvent) {
        if (keyEvent.getCode() != KeyCode.ENTER) return;
        auth();
    }
    @FXML
    private void onMouseClickClientDir(MouseEvent mouseEvent) {
        flgServer = false;
        String name = (String) lstClientDir.getSelectionModel().getSelectedItem();
        int clickCnt = mouseEvent.getClickCount();
        setDir(name, clickCnt);
    }
    @FXML
    private void onMouseClickServerDir(MouseEvent mouseEvent) {
        flgServer = true;
        String name = (String) lstServerDir.getSelectionModel().getSelectedItem();
        int clickCnt = mouseEvent.getClickCount();
        setDir(name, clickCnt);
    }
    @FXML
    private void onMouseClickCreateDir(MouseEvent mouseEvent) {
        String name = (String) JOptionPane.showInputDialog(null, "Имя папки: ", "Создать папку", JOptionPane.PLAIN_MESSAGE);
        createDir(name);
    }
    @FXML
    private void onMouseClickDelete(MouseEvent mouseEvent) {
        delete();
    }
    @FXML
    private void onMouseClickCopyFil(MouseEvent mouseEvent) {
        copyFil();
    }

    private void authOn() {
        mess = new Mess(MessageTypes.AUTH_ON);
        mess.setLogin(txtLogin.getText());
        mess.setPassword(pswPassword.getText());
        messResp = client.work(mess);

        switch (messResp.getCode()) {
            case OK : break;
            case ERR_LOGIN :
                txtLogin.setText("");
                pswPassword.setText("");
                break;
            case ERR_PASSWORD :
                pswPassword.setText("");
                break;
            default :
                txtLogin.setText("");
                pswPassword.setText("");
                break;
        }
        refreshForm();
    }
    private void authOff() {
        mess = new Mess(MessageTypes.AUTH_OFF);
        messResp = client.work(mess);

        txtLogin.setText("");
        pswPassword.setText("");

        refreshForm();
    }
    private void setDir(String name, int cntClick) {
        mess = new Mess(MessageTypes.DIR_SET);
        mess.setSelectName(name);
        mess.setFlgServer(flgServer);
        mess.setCntClick(cntClick);
        messResp = client.work(mess);

        refreshForm();
    }
    private void createDir(String name) {
        mess = new Mess(MessageTypes.FILE_ADD);
        mess.setSelectName(name);
        mess.setFlgServer(flgServer);
        messResp = client.work(mess);

        refreshForm();
    }
    private void delete() {
        mess = new Mess(MessageTypes.DIR_DEL);
        mess.setFlgServer(flgServer);
        messResp = client.work(mess);

        refreshForm();
    }
    private void copyFil() {
        mess = new Mess(MessageTypes.DIR_COPY);
        mess.setFlgServer(flgServer);
        messResp = client.work(mess);

        if (messResp.getCode() != ResultCodes.OK) {
            JOptionPane.showMessageDialog(null,
                    new String[] {"Не выбран файл!"},
                    "Ошибка копирования",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        refreshForm();
    }
    private void disconn() {
        mess = new Mess(MessageTypes.CONN_CLOSE);
        messResp = client.work(mess);
    }

    private void auth() {
        if (client.isAuth())
            authOff();
        else
            authOn();
    }
    public void windowClose() {
        disconn();
    }
    private void refreshForm() {
        panelAuth.setDisable(client.isAuth());
        btnConnect.setText(client.isAuth() ? "Отключиться" : "Подключиться");
        panelDir.setDisable(!client.isAuth());
        panelComand.setDisable(!client.isAuth());

        if (client.isAuth()) {
            setClientDirPanel();
            setServerDirPanel();
        }
    }
    private void setClientDirPanel() {
        messResp = getDirInfo(false);

        txtClientPath.setText(getPath(messResp, false));
        lstClientDir.setItems(FXCollections.observableArrayList(messResp.getListFile().toArray(new String[0])));
    }
    private void setServerDirPanel() {
        messResp = getDirInfo(true);

        txtServerPath.setText(getPath(messResp, true));
        lstServerDir.setItems(FXCollections.observableArrayList(messResp.getListFile().toArray(new String[0])));
    }
    private Mess getDirInfo(boolean flgServer) {
        mess = new Mess(MessageTypes.DIR_INFO);
        mess.setFlgServer(flgServer);
        return client.work(mess);
    }
    private String getPath(Mess messRespDirInfo, boolean flgServer) {
        String strPath;
        String name = messRespDirInfo.getSelectName();

        if (flgServer)
            strPath = messRespDirInfo.getDirPath() + "\\" + (messRespDirInfo.getValInt() == 0 ? name : "*");
        else
            strPath = messRespDirInfo.getDirPath() + (messRespDirInfo.isFlgDirRoot() ? "" : "\\") + (messRespDirInfo.getValInt() == 0 ? name : "*");

        return strPath;
    }
}
