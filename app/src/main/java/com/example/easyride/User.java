package com.example.easyride;

public abstract class User {

    private String userName;
    private String password;
    private String name;
    private boolean onTheWay;

    public void User(final String username, final String password, final String name) {
        this.userName = username;
        this.password = password;
        this.name = name;
        this.onTheWay = false;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getName() {
        return this.name;
    }

    public boolean getStatus() {
        return this.onTheWay;
    }

    public void setPW(final String pw) {
        this.password = pw;
    }

}
