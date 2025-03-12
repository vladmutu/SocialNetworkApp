package org.example.socialnetwork.Domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private Long id;
    private User sender;
    private List<User> receivers;
    private String text;
    private LocalDateTime date;
    private Message reply;

    public Message(User sender, List<User> receivers, String text, LocalDateTime date) {
        this.sender = sender;
        this.receivers = receivers;
        this.text = text;
        this.date = date;
        this.reply = null;
    }

    public Message(User sender, List<User> receivers, String text) {
        this.sender = sender;
        this.receivers = receivers;
        this.text = text;
        this.date = LocalDateTime.now();
        this.reply = null;
    }

    @Override
    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public List<User> getReceivers() {
        return receivers;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Message getReply() {
        return reply;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceivers(List<User> receivers) {
        this.receivers = receivers;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }
}
