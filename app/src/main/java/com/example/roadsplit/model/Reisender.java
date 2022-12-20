package com.example.roadsplit.model;

import java.util.List;

public class Reisender {
    private Long id;
    private String email;
    private String nickname;
    private List<Reise> reisen;

    public Reisender(Long id, String email, String nickname, List<Reise> reisen) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.reisen = reisen;
    }

    public Reisender() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<Reise> getReisen() {
        return reisen;
    }

    public void setReisen(List<Reise> reisen) {
        this.reisen = reisen;
    }

    @Override
    public String toString() {
        return "Reisender{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", reisen=" + reisen +
                '}';
    }
}
