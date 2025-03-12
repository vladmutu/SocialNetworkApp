package org.example.socialnetwork.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.socialnetwork.Domain.Message;
import org.example.socialnetwork.Service.Service;
import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Utils.Constants;

import java.util.Objects;

public class MessageWindow{
    public Button backToProfileView;
    ObservableList<Message> messagesModel = FXCollections.observableArrayList();

    public ListView<User> listFriends;
    public ListView<Message> listMessages;
    public TextArea message;

    private Service service;

    private User user;

    public void setService(Service service, User user) {
        this.service = service;
        this.user = user;
        loadFriends();
    }

    @FXML
    public void initialize() {
        listFriends.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadListMessages(user.getId(), newValue.getId());
            }
        });
    }

    @FXML
    public void handleSendMessage() {
        User selectedFriend = listFriends.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            Long idFrom = user.getId();
            Long idTo = selectedFriend.getId();

            String msg = message.getText();

            service.addMessage(idFrom, idTo, msg);
            loadListMessages(idFrom, idTo);

            message.clear();
            listFriends.getSelectionModel().select(selectedFriend);
        } else {
            Dialog dialog = new Dialog();
            dialog.setContentText("You must select a friend to send a message!");
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(okButton);
            dialog.showAndWait();
        }
    }

    public void loadListMessages(Long id1, Long id2) {
        listMessages.getItems().clear();
        messagesModel.clear();
        messagesModel.addAll(service.getMessages(id1, id2));

        listMessages.setItems(messagesModel);
        listMessages.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                } else {
                    if(Objects.equals(msg.getSender().getId(), user.getId())) {
                        setText("You: " + msg.getText() + " at: " + msg.getDate().format(Constants.DATE_TIME_FORMATTER));
                    } else {
                        setText(msg.getSender().getFirstName() + ": " + msg.getText() + " at: " + msg.getDate().format(Constants.DATE_TIME_FORMATTER));
                    }
                }
            }
        });
    }

    private void loadFriends() {
        ObservableList<User> friends = FXCollections.observableArrayList(service.getUsersFriends(user.getId()));
        listFriends.setItems(friends);
        listFriends.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty || friend == null) {
                    setText(null);
                } else {
                    setText(friend.getFirstName() + " " + friend.getLastName() + " " + friend.getEmail());
                }
            }
        });
    }

    public void backToProfile(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }
}
