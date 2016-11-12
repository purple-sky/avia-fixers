package com.aviafix.client;

import com.google.common.collect.ImmutableBiMap;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.auth.Authenticator;

import java.util.Map;
import java.util.Optional;


/**
 * Created by AlexB on 2016-11-05.
 */
public class BasicAuthenticator implements Authenticator <BasicCredentials, User>{
    private static final Map<String, String> VALID_USERS = ImmutableBiMap.of(
            "customer", Roles.CUSTOMER,
            "finance",Roles.FINANCE,
            "repair",Roles.REPAIR
    );

    @Override
    public Optional<User> authenticate (BasicCredentials credentials) throws AuthenticationException {
        if (VALID_USERS.containsKey(credentials.getUsername()) && "password".equals(credentials.getPassword()))
        {
            return Optional.of(new User(credentials.getUsername(), VALID_USERS.get(credentials.getUsername())));
        }
        return Optional.empty();
    }
}
