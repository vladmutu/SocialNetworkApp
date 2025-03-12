package org.example.socialnetwork.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.socialnetwork.Domain.Friendship;
import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Service.Service;
import org.example.socialnetwork.Utils.Events.UserEntityChangeEvent;
import org.example.socialnetwork.Utils.Observer.Observer;

import java.util.Objects;

public class AddFriendWindow implements Observer<UserEntityChangeEvent> {
    private Service service;
    private User user;
    ObservableList<User> model = FXCollections.observableArrayList();
    public TableView<User> tableView;
    public TableColumn<User, String> tableColumnFirstName;
    public TableColumn<User, String> tableColumnLastName;
    public TableColumn<User, String> tableColumnEmail;

    public void setService(Service service, User user) {
        this.service = service;
        this.user = user;
        initModel();
        initializeView();
    }

    public void initializeView() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableView.setItems(model);
    }
    public void initModel() {
        Iterable<User> users = service.getAllUsers();
        Iterable<Friendship> frienships = service.getAllFriendships();
        //remove from users the current user
        users.forEach(user -> {
            if(!Objects.equals(user.getId(), this.user.getId()) && !service.isFriendship(user.getId(), this.user.getId())) {
                model.add(user);
            }
        });
    }

    public void handleSendFriendRequest(ActionEvent actionEvent) {
        User selectedUser = (User) tableView.getSelectionModel().getSelectedItem();
        if(selectedUser == null) {
            return;
        }
        try{
            service.sendFriendRequest(user.getId(), selectedUser.getId());
            Dialog dialog = new Dialog();
            dialog.setContentText("Friend request sent");
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);
            dialog.showAndWait();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }

    public void backToFriends(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }
}
