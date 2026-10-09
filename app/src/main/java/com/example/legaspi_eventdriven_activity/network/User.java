package com.example.legaspi_eventdriven_activity.network;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("LastName")
    private String lastName;

    @SerializedName("FirstName")
    private String firstName;

    @SerializedName("Email")
    private String email;

    @SerializedName("Password")
    private String password;

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
