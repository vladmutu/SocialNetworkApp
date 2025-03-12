package org.example.socialnetwork.Domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<Long> {

    private Long id1;
    private Long id2;
    private LocalDateTime friendsSince;
    private boolean pending;

    public Friendship(Long id1, Long id2, LocalDateTime date) {
        this.id1 = id1;
        this.id2 = id2;
        this.friendsSince = date;
        this.pending = true;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public Long getId1() {
        return id1;
    }

    public Long getId2() {
        return id2;
    }

    public LocalDateTime getDate() {
        return friendsSince;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

    public void setId2(Long id2) {
        this.id2 = id2;
    }

    public void setDate(LocalDateTime date) {
        this.friendsSince = date;
    }

    public boolean contains(Long id) {
        return id1.equals(id) || id2.equals(id);
    }

    @Override
    public String toString() {
        return "Friendship{"
                + "id="
                + getId()
                + ", id1="
                + id1
                + ", id2="
                + id2
                + ", date="
                + friendsSince
                + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Friendship)) {
            return false;
        }
        Friendship f = (Friendship) obj;
        return id1.equals(f.id1) && id2.equals(f.id2) && friendsSince.equals(f.friendsSince) && pending == f.pending;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
