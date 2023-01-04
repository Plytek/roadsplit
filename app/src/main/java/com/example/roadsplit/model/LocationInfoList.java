package com.example.roadsplit.model;

import java.util.List;

public class LocationInfoList {
    List<LocationInfo> locationInfo;

    public LocationInfoList(List<LocationInfo> locationInfo) {
        this.locationInfo = locationInfo;
    }

    public LocationInfoList() {
    }

    public List<LocationInfo> getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(List<LocationInfo> locationInfo) {
        this.locationInfo = locationInfo;
    }
}
