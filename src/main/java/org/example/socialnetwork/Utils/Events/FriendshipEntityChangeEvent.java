package org.example.socialnetwork.Utils.Events;

import org.example.socialnetwork.Domain.Friendship;

public class FriendshipEntityChangeEvent implements Event {
    private final ChangeEventType changeEventType;
    private final Friendship data;

    public FriendshipEntityChangeEvent(ChangeEventType changeEventType, Friendship data) {
        this.changeEventType = changeEventType;
        this.data = data;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    public Friendship getData() {
        return data;
    }
}

