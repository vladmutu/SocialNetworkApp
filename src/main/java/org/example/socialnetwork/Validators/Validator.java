package org.example.socialnetwork.Validators;

@FunctionalInterface
public interface Validator<T> {
    void validate(T entity) throws ServiceException;
}