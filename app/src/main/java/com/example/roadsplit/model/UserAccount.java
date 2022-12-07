package com.example.roadsplit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
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
}
