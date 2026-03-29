package org.example.socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.socialnetwork.Controllers.StartWindow;
import org.example.socialnetwork.Service.Service;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class SocialNetworkStart extends Application {
    private double xOffsetForChildWindow;
    private double yOffsetForChildWindow;
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(SocialNetworkApplication.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Service service = context.getBean(Service.class);
        FXMLLoader loader = new FXMLLoader(SocialNetworkStart.class.getResource("/org/example/socialnetwork/start-window.fxml"));
        Parent root = loader.load();
        root.setOnMousePressed(event -> {
            xOffsetForChildWindow = event.getSceneX();
            yOffsetForChildWindow = event.getSceneY();
        });
        Stage startStage = new Stage();
        Scene startScene = new Scene(root, 600, 600);
        root.setOnMouseDragged(event -> {
            startStage.setX(event.getScreenX() - xOffsetForChildWindow);
            startStage.setY(event.getScreenY() - yOffsetForChildWindow);
        });
        startStage.initStyle(StageStyle.TRANSPARENT);

        startScene.setFill(Color.TRANSPARENT);
        StartWindow startWindow = loader.getController();
        startWindow.setService(service);
        startStage.setScene(startScene);
        startStage.show();
    }

    @Override
    public void stop() {
        if (context != null) {
            context.close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}