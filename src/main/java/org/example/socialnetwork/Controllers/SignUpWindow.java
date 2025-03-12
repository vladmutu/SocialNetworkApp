package org.example.socialnetwork.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Service.Service;
import org.example.socialnetwork.Validators.ServiceException;
import org.example.socialnetwork.Validators.ValidationException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.io.File;
import java.util.Base64;

public class SignUpWindow {


    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public TextField passwordFieldVisible;public CheckBox showPassword;

    public Button uploadProfilePicture;

    public ImageView uploadedImage;

    public HBox rotationButtonsHBox;

    private Service service;

    public TextField firstNameField;

    public TextField lastNameField;

    public TextField emailField;

    public PasswordField passwordField;

    private double currentRotation;

    public void setService(Service service) {
        this.service = service;
    }

    public static String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public void registerAction(ActionEvent actionEvent) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()){
            Dialog dialog = new Dialog();
            dialog.setContentText("Please fill in all the fields");
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);
            dialog.showAndWait();
        }
        if(!email.contains("@gmail.com") && !email.contains("@yahoo.com") && !email.contains("@hotmail.com")) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Invalid email");
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);
            dialog.showAndWait();
        }

        if(password.length() < 6) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Password must be at least 6 characters long");
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);
            dialog.showAndWait();
        }
        Iterable<User> users = service.getAllUsers();
        for(User user : users) {
            if(user.getEmail().equals(email)) {
                Dialog dialog = new Dialog();
                dialog.setContentText("Email already in use");
                ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(closeButton);
                dialog.showAndWait();
                return;
            }
        }
        try {
            String pass = hashPassword(password, email.getBytes());
            byte[] rawProfilePicture = getRawBytes(uploadedImage.getImage());
            User user = new User(firstName, lastName, email, pass, rawProfilePicture);
            service.addUser(user);
            Dialog dialog = new Dialog();
            dialog.setContentText("Account created successfully");
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);
            dialog.showAndWait();
                ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
        }catch (ValidationException |ServiceException e) {
            Dialog dialog = new Dialog();
            dialog.setContentText(e.getMessage());
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);
            dialog.showAndWait();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void backToLogin(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void showHidePassword(ActionEvent actionEvent) {
        if(showPassword.isSelected()) {
            passwordFieldVisible.setText(passwordField.getText());
            passwordFieldVisible.setManaged(true);
            passwordFieldVisible.setVisible(true);
            passwordField.setManaged(false);
            passwordField.setVisible(false);
        }
        else {
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            passwordField.setText(passwordField.getText());
        }
    }

    public void chooseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null) {
            Image image = new Image(file.toURI().toString());
            uploadedImage.setFitHeight(60);
            uploadedImage.setFitWidth(60);
            uploadedImage.setImage(image);
            createRotationButtons();
        }
    }

    private void createRotationButtons() {
        Button rotateLeftButton = new Button("⟲ Rotate Left");
        rotateLeftButton.setId("rotateLeftButton");
        rotateLeftButton.setOnAction(e -> rotateImage(-90));
        Button rotateRightButton = new Button("⟳ Rotate Right");
        rotateRightButton.setId("rotateRightButton");
        rotateRightButton.setOnAction(e -> rotateImage(90));
        rotationButtonsHBox.getChildren().addAll(rotateLeftButton, rotateRightButton);
    }

    private void rotateImage(double angle) {
        currentRotation += angle;
        uploadedImage.setRotate(currentRotation);

    }

    public byte[] getRawBytes(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int[] pixelData = new int[width * height];
        PixelReader pixelReader = image.getPixelReader();
        pixelReader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixelData, 0, width);
        ByteBuffer buffer = ByteBuffer.allocate(8 + pixelData.length * 4);
        buffer.putInt(width);
        buffer.putInt(height);
        for (int argb : pixelData) {
            buffer.putInt(argb);
        }
        return buffer.array();
    }
}
