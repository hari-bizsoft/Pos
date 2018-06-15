package com.bizsoft.pos.dataobject;

import java.util.ArrayList;

/**
 * Created by shri on 11/7/17.
 */

public class Cart {

    String id;
    String user;
    String size;
    ArrayList<Items> items = new ArrayList<Items>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }
}
