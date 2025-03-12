package org.example.socialnetwork.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Service.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;


public class StartWindow {
    private double xOffsetForChildWindow;
    private double yOffsetForChildWindow;

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    private Service service;

    public TextField emailField;

    public PasswordField passwordField;

    public TextField passwordFieldVisible;

    public CheckBox showPassword;

    public void setService(Service service) {
        this.service = service;
    }

    public static String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hashedPassword);
    }


    public void SignIn(ActionEvent actionEvent) throws Exception {
        String email = emailField.getText();
        String password = hashPassword(passwordField.getText(), email.getBytes());
        if(email.isEmpty() || password.isEmpty()) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Error");
            dialog.setHeaderText("Error");
            dialog.setContentText("Please fill in all the fields");
        }
        if(!email.contains("@gmail.com") && !email.contains("@yahoo.com")) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Error");
            dialog.setHeaderText("Error");
            dialog.setContentText("Invalid email");
        }
        if(password.length() < 6) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Error");
            dialog.setHeaderText("Error");
            dialog.setContentText("Password must be at least 6 characters long");
        }
        List<User> users = StreamSupport.stream(service.getAllUsers().spliterator(), false).toList()
                .stream().filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password)).toList();
        if(users.isEmpty()) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Error");
            dialog.setHeaderText("Error");
            dialog.setContentText("Invalid email or password");
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);
            dialog.showAndWait();
        }
        else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/socialnetwork/profile-view.fxml"));
                Parent root = loader.load();
                root.setOnMousePressed(event -> {
                    xOffsetForChildWindow = event.getSceneX();
                    yOffsetForChildWindow = event.getSceneY();
                });
                Stage mainStage = new Stage();
                root.setOnMouseDragged(event -> {
                    mainStage.setX(event.getScreenX() - xOffsetForChildWindow);
                    mainStage.setY(event.getScreenY() - yOffsetForChildWindow);
                });
                Scene mainScene = new Scene(root, 750, 500);
                mainStage.initStyle(StageStyle.TRANSPARENT);
                mainScene.setFill(Color.TRANSPARENT);
                mainStage.setScene(mainScene);
                ProfileView profileView = loader.getController();
                profileView.setServiceAndUser(service, users.get(0));
                mainStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToSignUp(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/socialnetwork/sign-up-window.fxml"));
            Parent root = loader.load();
            root.setOnMousePressed(event -> {
                xOffsetForChildWindow = event.getSceneX();
                yOffsetForChildWindow = event.getSceneY();
            });
            //a new stage for the sign-up window
            Stage signUpStage = new Stage();

            root.setOnMouseDragged(event -> {
                signUpStage.setX(event.getScreenX() - xOffsetForChildWindow);
                signUpStage.setY(event.getScreenY() - yOffsetForChildWindow);
            });
            //initializing a 400x300 scene
            Scene signUpScene = new Scene(root, 600, 600);
            //set the stage transparent to round corners
            signUpStage.initStyle(StageStyle.TRANSPARENT);
            //set the scene fill property transparent, to fill the scene transparent
            signUpScene.setFill(Color.TRANSPARENT);
            //load the controller
            SignUpWindow signUpWindow = loader.getController();
            //set the service
            signUpWindow.setService(service);
            signUpStage.setScene(signUpScene);
            signUpStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showHidePassword(ActionEvent actionEvent) {
        if(showPassword.isSelected()) {
            passwordFieldVisible.setManaged(true);
            passwordFieldVisible.setVisible(true);
            passwordFieldVisible.setText(passwordField.getText());
            passwordField.setManaged(false);
            passwordField.setVisible(false);
        }
        else {
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            passwordField.setText(passwordField.getText());
        }
    }

    public void stop(ActionEvent actionEvent) {
        System.exit(0);
    }
}
