package org.example.socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.socialnetwork.Controllers.StartWindow;
import org.example.socialnetwork.Repository.FriendshipDBRepository;
import org.example.socialnetwork.Repository.MessageDBRepository;
import org.example.socialnetwork.Repository.UserDBRepository;
import org.example.socialnetwork.Service.Service;
import org.example.socialnetwork.Validators.FriendshipValidator;
import org.example.socialnetwork.Validators.UserValidator;

import java.io.IOException;

public class SocialNetworkStart extends Application {
    private double xOffsetForChildWindow;
    private double yOffsetForChildWindow;
    @Override
    public void start(Stage primaryStage) throws IOException {
        //Modificati aceste variabile pentru a realiza conexiunea la baza de date
        //Exemplu: url = "jdbc:postgresql://localhost:5432/socialnetwork"; username = "postgres"; password = "password";
        String url = "";
        String username = "";
        String password = "";
        UserDBRepository userDBRepository = new UserDBRepository(url, username, password, new UserValidator());
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(url, username, password, new FriendshipValidator());
        MessageDBRepository messageDBRepository = new MessageDBRepository(userDBRepository, url, username, password);
        Service service = new Service(userDBRepository, friendshipDBRepository, messageDBRepository);
        FXMLLoader loader  = new FXMLLoader(SocialNetworkStart.class.getResource("start-window.fxml"));
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

    public static void main(String[] args) {
        launch();
    }
}