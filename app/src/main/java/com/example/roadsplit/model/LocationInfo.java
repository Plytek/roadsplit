package com.example.roadsplit.model;

import java.util.Arrays;
import java.util.List;

public class LocationInfo {
    private String place_id;
    private String osm_id;
    private String osm_type;
    private String licence;
    private String lat;
    private String lon;
    private String[] boundingbox;
    private String clazz;
    private String type;
    private String display_name;
    private String display_place;
    private String display_address;
    private Location address;

    public LocationInfo() {
    }

    public LocationInfo(String place_id, String osm_id, String osm_type, String licence, String lat, String lon, String[] boundingbox, String clazz, String type, String display_name, String display_place, String display_address, Location address) {
        this.place_id = place_id;
        this.osm_id = osm_id;
        this.osm_type = osm_type;
        this.licence = licence;
        this.lat = lat;
        this.lon = lon;
        this.boundingbox = boundingbox;
        this.clazz = clazz;
        this.type = type;
        this.display_name = display_name;
        this.display_place = display_place;
        this.display_address = display_address;
        this.address = address;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(String osm_id) {
        this.osm_id = osm_id;
    }

    public String getOsm_type() {
        return osm_type;
    }

    public void setOsm_type(String osm_type) {
        this.osm_type = osm_type;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String[] getBoundingbox() {
        return boundingbox;
    }

    public void setBoundingbox(String[] boundingbox) {
        this.boundingbox = boundingbox;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplay_place() {
        return display_place;
    }

    public void setDisplay_place(String display_place) {
        this.display_place = display_place;
    }

    public String getDisplay_address() {
        return display_address;
    }

    public void setDisplay_address(String display_address) {
        this.display_address = display_address;
    }

    public Location getAddress() {
        return address;
    }

    public void setAddress(Location address) {
        this.address = address;
    }
}
