package window;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppStart extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxForm.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Файловое Хранилище");
        primaryStage.setScene(new Scene(root, 1200, 900));

        Controller contrl = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> contrl.windowClose());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
