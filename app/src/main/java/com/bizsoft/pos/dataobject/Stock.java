package com.bizsoft.pos.dataobject;

import java.util.ArrayList;

/**
 * Created by shri on 20/6/17.
 */

public class Stock {


    String id;
    String name;
    String stockSize;


    ArrayList<Items> items = new ArrayList<Items>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStockSize() {
        return stockSize;
    }

    public void setStockSize(String stockSize) {
        this.stockSize = stockSize;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }
}
