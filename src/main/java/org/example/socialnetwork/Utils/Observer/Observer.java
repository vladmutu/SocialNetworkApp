package org.example.socialnetwork.Utils.Observer;

import org.example.socialnetwork.Utils.Events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}