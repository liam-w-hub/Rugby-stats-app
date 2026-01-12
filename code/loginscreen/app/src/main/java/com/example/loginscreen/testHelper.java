package com.example.loginscreen;


// helper class used to make methods independent of android-specific components
public class testHelper {
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public boolean isValidTeamName(String name) {
        return name != null && !name.isEmpty();
    }

}
