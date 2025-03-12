package org.example.socialnetwork.Utils.Events;

import org.example.socialnetwork.Domain.User;

public class UserEntityChangeEvent implements Event {
    private ChangeEventType changeEventType;
    private User data;
    private User oldData;

    public UserEntityChangeEvent(ChangeEventType changeEventType, User data) {
        this.changeEventType = changeEventType;
        this.data = data;
    }

    public UserEntityChangeEvent(ChangeEventType changeEventType, User data, User oldData) {
        this.changeEventType = changeEventType;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
