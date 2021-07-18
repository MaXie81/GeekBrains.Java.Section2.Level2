package client.window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FXApplication extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxForm.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Файловое Хранилище");
        primaryStage.setScene(new Scene(root, 1200, 900));

        primaryStage.getIcons().add(new Image("images/icon.png"));

        Controller contrl = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> contrl.windowClose());
        primaryStage.show();
    }

    public static void start(String[] args) {
        launch(args);
    }
}
