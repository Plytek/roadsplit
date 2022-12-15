package com.example.roadsplit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class UserAccount {

    private int id;
    private String uniquename;
    private String nickname;
    private List<Reise> reisen;

    public UserAccount() {
    }

    public UserAccount(int id, String uniquename, String nickname) {
        this.id = id;
        this.uniquename = uniquename;
        this.nickname = nickname;
    }

    public UserAccount(int id, String uniquename, String nickname, List<Reise> reisen) {
        this.id = id;
        this.uniquename = uniquename;
        this.nickname = nickname;
        this.reisen = reisen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<Reise> getReisen() {
        return reisen;
    }

    public void setReisen(List<Reise> reisen) {
        this.reisen = reisen;
    }
}
