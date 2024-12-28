package com.example.guiserver;

public class LoginResult {
    private boolean credentialsExist;
    private String UserType;
    private int UserId;

    public LoginResult(boolean credentialsExist, String userType, int userId) {
        this.credentialsExist = credentialsExist;
        UserType = userType;
        UserId = userId;
    }

    public boolean isCredentialsExist() {
        return credentialsExist;
    }

    public void setCredentialsExist(boolean credentialsExist) {
        this.credentialsExist = credentialsExist;
    }

    public String UserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }
}
