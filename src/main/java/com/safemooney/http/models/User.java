package com.safemooney.http.models;

public class User {

    private int id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String tokenkey;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenkey() {
        return tokenkey;
    }

    public void setTokenkey(String tokenkey) {
        this.tokenkey = tokenkey;
    }

    @Override
    public String toString() {
        return "username: " + this.username +
                "\npassword: " + this.password +
                "\ntokenkey: " + this.tokenkey;
    }
}
