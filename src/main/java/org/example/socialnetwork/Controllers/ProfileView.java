package org.example.socialnetwork.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Service.Service;
import org.example.socialnetwork.Utils.Events.UserEntityChangeEvent;
import org.example.socialnetwork.Utils.Observer.Observer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProfileView implements Observer<UserEntityChangeEvent> {
    public Label greetingLabel;
    public Button buttonFriends;
    public Button buttonMessages;
    public Button buttonLogOut;
    public Button editProfile;
    public Button deleteProfile;
    public ImageView friendRequestNotification;
    public ImageView profileImage;

    private Service service;

    private User user;

    private int friendRequestCount;

    private double xOffsetForChildWindow;
    private double yOffsetForChildWindow;

    public void setServiceAndUser(Service service, User user) {
        this.service = service;
        this.user = user;
        String firstName = user.getFirstName();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        greetingLabel.setText("Welcome back " + firstName);
        service.addObserver(this);
        Image profilePicture = convertBytesToImage(user.getProfilePicture());
        profileImage.setImage(profilePicture);
        profileImage.setClip(new Circle(50, 50, 50));
        friendRequestCount = service.getFriendRequests(user.getId()).size();
        if (friendRequestCount == 0) {
            friendRequestNotification.setVisible(false);
            friendRequestNotification.setManaged(false);
        } else {
            friendRequestNotification.setVisible(true);
            friendRequestNotification.setManaged(true);
        }
    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        friendRequestCount = service.getFriendRequests(user.getId()).size();
        if (friendRequestCount == 0) {
            friendRequestNotification.setVisible(false);
            friendRequestNotification.setManaged(false);
        } else {
            friendRequestNotification.setVisible(true);
            friendRequestNotification.setManaged(true);
        }
    }

    public void handleLogOut(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void handleEditProfile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/socialnetwork/update-user.fxml"));
            Parent root = loader.load();
            root.setOnMousePressed(event -> {
                xOffsetForChildWindow = event.getSceneX();
                yOffsetForChildWindow = event.getSceneY();
            });
            //a new stage for the sign-up window
            Stage editProfileStage = new Stage();

            root.setOnMouseDragged(event -> {
                editProfileStage.setX(event.getScreenX() - xOffsetForChildWindow);
                editProfileStage.setY(event.getScreenY() - yOffsetForChildWindow);
            });
            //initializing a 400x300 scene
            Scene editProfileScene = new Scene(root, 300, 450);
            //set the stage transparent to round corners
            editProfileStage.initStyle(StageStyle.TRANSPARENT);
            //set the scene fill property transparent, to fill the scene transparent
            editProfileScene.setFill(Color.TRANSPARENT);
            UpdateUser updateUser = loader.getController();
            updateUser.setService(service, user);
            editProfileStage.setScene(editProfileScene);
            editProfileStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteProfile(ActionEvent actionEvent) {
        service.removeFriendships(user.getId());
        service.removeMessages(user.getId());
        Dialog dialog = new Dialog();
        dialog.setContentText("User deleted");
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        dialog.showAndWait();
        service.removeUser(user.getId());
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void handleGoToMessages(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/socialnetwork/message-window.fxml"));
            Parent root = loader.load();
            root.setOnMousePressed(event -> {
                xOffsetForChildWindow = event.getSceneX();
                yOffsetForChildWindow = event.getSceneY();
            });
            Stage messageStage = new Stage();
            root.setOnMouseDragged(event -> {
                messageStage.setX(event.getScreenX() - xOffsetForChildWindow);
                messageStage.setY(event.getScreenY() - yOffsetForChildWindow);
            });
            Scene messageScene = new Scene(root, 600, 550);
            messageStage.initStyle(StageStyle.TRANSPARENT);
            messageScene.setFill(Color.TRANSPARENT);
            messageStage.setScene(messageScene);
            MessageWindow messageWindow = loader.getController();
            messageWindow.setService(service, user);
            messageStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleGoToFriends(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/socialnetwork/main-window.fxml"));
            Parent root = loader.load();
            root.setOnMousePressed(event -> {
                xOffsetForChildWindow = event.getSceneX();
                yOffsetForChildWindow = event.getSceneY();
            });
            Stage friendsStage = new Stage();
            root.setOnMouseDragged(event -> {
                friendsStage.setX(event.getScreenX() - xOffsetForChildWindow);
                friendsStage.setY(event.getScreenY() - yOffsetForChildWindow);
            });
            Scene friendsScene = new Scene(root, 800, 600);
            friendsStage.initStyle(StageStyle.TRANSPARENT);
            friendsScene.setFill(Color.TRANSPARENT);
            friendsStage.setScene(friendsScene);
            MainWindow mainWindow = loader.getController();
            mainWindow.setServiceAndUser(service, user);
            friendsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image convertBytesToImage(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        int width = buffer.getInt();  // Read width (first 4 bytes)
        int height = buffer.getInt(); // Read height (next 4 bytes)

        int[] pixelData = new int[width * height];

        // Read pixel data
        for (int i = 0; i < pixelData.length; i++) {
            pixelData[i] = buffer.getInt();
        }

        // Create WritableImage and set pixels
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixelData, 0, width);

        return writableImage;
    }
}
