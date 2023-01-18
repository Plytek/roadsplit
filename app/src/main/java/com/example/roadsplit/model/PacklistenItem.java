package com.example.roadsplit.model;

public class PacklistenItem {
    private long id;
    private String itemname;
    private boolean done;

    public PacklistenItem(long id, String itemname, boolean done) {
        this.id = id;
        this.itemname = itemname;
        this.done = done;
    }

    public PacklistenItem() {
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getItemname() {
        return itemname;
    }
    public void setItemname(String itemname) {
        this.itemname = itemname;
    }
    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }
}
