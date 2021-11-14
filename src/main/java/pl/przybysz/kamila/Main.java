package pl.przybysz.kamila;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import pl.przybysz.kamila.controller.MainStageController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/MainStage.fxml"));
        AnchorPane anchorPane = loader.load();
// Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
        MainStageController mainStageController = loader.getController();
        mainStageController.setMainStage(primaryStage);
        Scene scene = new Scene(anchorPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Aplikacja APO");
        primaryStage.setResizable(false);
        //zamkniecie glownego okna powoduje zamkniecie wszytskich pozostałych
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            mainStageController.closeAllStages();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        OpenCV.loadShared();
        System.out.println(Core.VERSION);
        launch(args);
    }
}
