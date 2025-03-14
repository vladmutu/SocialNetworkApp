package org.example.socialnetwork.Domain;

import java.time.LocalDateTime;

public class FriendRequest {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime friendsSince;
    private Long id;

    public FriendRequest(Long id, String firstName, String lastName, String email, LocalDateTime date) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.friendsSince = date;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFriendsSince(LocalDateTime date) {
        this.friendsSince = date;
    }

}
