package com.example.roadsplit.model;

public class Location {
    private String name;
    private String city;
    private String county;
    private String state;
    private String postcode;
    private String country;
    private String country_code;

    public Location() {
    }

    public Location(String name, String city, String county, String state, String postcode, String country, String country_code) {
        this.name = name;
        this.city = city;
        this.county = county;
        this.state = state;
        this.postcode = postcode;
        this.country = country;
        this.country_code = country_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
