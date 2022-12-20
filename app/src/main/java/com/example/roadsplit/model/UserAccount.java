package com.example.roadsplit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class UserAccount {

    private int id;
    private String email;
    private String nickname;
    private String password;
    private boolean firsttimelogin;

    public UserAccount() {
    }

    public UserAccount(int id, String email, String nickname, String password, boolean firsttimelogin) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.firsttimelogin = firsttimelogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFirsttimelogin() {
        return firsttimelogin;
    }

    public void setFirsttimelogin(boolean firsttimelogin) {
        this.firsttimelogin = firsttimelogin;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", firsttimelogin=" + firsttimelogin +
                '}';
    }
}
