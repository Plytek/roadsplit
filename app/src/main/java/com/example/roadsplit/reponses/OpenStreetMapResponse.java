package com.example.roadsplit.reponses;

import java.util.List;

public class OpenStreetMapResponse {
    private double version;
    private String generator;
    private OSM3S osm3s;
    private List<Element> elements;

    public static class OSM3S {
        private String timestamp_osm_base;
        private String copyright;
    }

    public static class Element {
        private String type;
        private long id;
        private double lat;
        private double lon;
        public Tags tags;

        public static class Tags {
            private String historic;
            public String image;
            private String memorial_addr;
            private String memorial_addr_city;
            private String memorial_addr_housenumber;
            private String memorial_addr_postcode;
            private String memorial_addr_street;
            private String memorial_addr_suburb;
            private String memorial_text;
            private String memorial_type;
            private String name;
            private String network;
            private String website;
        }
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public OSM3S getOsm3s() {
        return osm3s;
    }

    public void setOsm3s(OSM3S osm3s) {
        this.osm3s = osm3s;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
