package com.mba.my_mechanic.models;

public class User {
    public String email;
    public String first_name;
    public String last_name;
    public String password;
    public String role;

    public User() {
        // Firestore requires a public no-arg constructor
    }

    public User(String email, String first_name, String last_name, String password, String role) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.role = role;
    }
}