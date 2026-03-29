package org.example.socialnetwork.Utils.Events;

import org.example.socialnetwork.Domain.Message;

public class MessageEntityChangeEvent implements Event {
    private final ChangeEventType changeEventType;
    private final Message data;

    public MessageEntityChangeEvent(ChangeEventType changeEventType, Message data) {
        this.changeEventType = changeEventType;
        this.data = data;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    public Message getData() {
        return data;
    }
}

