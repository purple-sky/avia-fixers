package com.aviafix.client;

import io.dropwizard.auth.Authorizer;

/**
 * Created by AlexB on 2016-11-05.
 */
public class UserAuthorizer implements Authorizer<User>{
    @Override
    public boolean authorize(User user, String role) {

        return user.getName().equals(user.getName()) &&
                user.getRole().equals(role);
    }
}
