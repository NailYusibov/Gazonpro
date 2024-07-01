package com.auth.provider.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.*;
import org.keycloak.storage.adapter.AbstractUserAdapter;

class CustomUser extends AbstractUserAdapter {

    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Date birthDate;
    private Set<RoleModel> roles;

    private CustomUser(KeycloakSession session, RealmModel realm,
                       ComponentModel storageProviderModel,
                       String username,
                       String email,
                       String firstName,
                       String lastName,
                       Date birthDate ) {
        super(session, realm, storageProviderModel);
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;

    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL,getEmail());
        attributes.add(UserModel.FIRST_NAME,getFirstName());
        attributes.add(UserModel.LAST_NAME,getLastName());
        attributes.add("birthDate",getBirthDate().toString());
        return attributes;
    }

    static class Builder {
        private final KeycloakSession session;
        private final RealmModel realm;
        private final ComponentModel storageProviderModel;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Date birthDate;

        Builder(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel,String username) {
            this.session = session;
            this.realm = realm;
            this.storageProviderModel = storageProviderModel;
            this.username = username;
        }

        Builder email(String email) {
            this.email = email;
            return this;
        }

        Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        Builder birthDate(Date birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        CustomUser build() {
            return new CustomUser(
                    session,
                    realm,
                    storageProviderModel,
                    username,
                    email,
                    firstName,
                    lastName,
                    birthDate);
        }
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new LegacyUserCredentialManager(session, realm, this);
    }

    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        return roles;
    }

    public void setRoles(ResultSet resultSet) throws SQLException {
        Set<RoleModel> roleModelSet = new HashSet<>();
        while (resultSet.next()) {
            roleModelSet.add(new UserRoleModel(resultSet.getString("name"), realm));
        }
        if (!roleModelSet.isEmpty()) {
            this.roles = roleModelSet;
        }
    }
}