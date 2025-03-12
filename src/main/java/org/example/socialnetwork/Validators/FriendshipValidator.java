package org.example.socialnetwork.Validators;

import org.example.socialnetwork.Domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ServiceException {
        if(entity.getId1() == null || entity.getId2() == null)
            throw new ValidationException("ID-ul nu poate fi null");
        if(entity.getId1().equals(entity.getId2())) {
            throw new ValidationException("ID-urile trebuie sa fie diferite");
        }
    }
}
