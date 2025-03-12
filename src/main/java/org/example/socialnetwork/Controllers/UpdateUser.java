package org.example.socialnetwork.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Service.Service;
import org.example.socialnetwork.Validators.ServiceException;

public class UpdateUser {
    public TextField textFieldFirstName;

    public TextField textFieldLastName;

    public TextField textFieldPassword;

    public TextField textFieldEmail;

    private Service service;

    private User u;

    public void setService(Service service, User u) {
        this.service = service;
        this.u = u;
    }

    public void handleUpdate(ActionEvent actionEvent) {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
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
        try {
            User newUser = new User(firstName, lastName, email, password, u.getProfilePicture());
            newUser.setId(u.getId());
            try {
                service.updateUser(newUser);
                Dialog dialog = new Dialog();
                dialog.setContentText("User updated successfully");
                ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(closeButton);
                dialog.showAndWait();
            }catch (ServiceException e){
                Dialog dialog = new Dialog();
                dialog.setContentText(e.getMessage());
                ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(closeButton);
                dialog.showAndWait();
            }
        } catch (ServiceException e) {
            Dialog dialog = new Dialog();
            dialog.setContentText(e.getMessage());
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);
            dialog.showAndWait();
        }
        //close the window
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void backToProfile(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }
}
