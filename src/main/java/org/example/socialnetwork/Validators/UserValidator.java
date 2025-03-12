package org.example.socialnetwork.Validators;


import org.example.socialnetwork.Domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ServiceException {
        //TODO: implement method validate
        if(entity.getFirstName().isEmpty() || entity.getLastName().isEmpty())
            throw new ValidationException("Utilizatorul nu este valid. Numele nu poate fi null.");
    }
}
