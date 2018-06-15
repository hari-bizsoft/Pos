package com.bizsoft.pos.dataobject;

import java.util.ArrayList;

/**
 * Created by shri on 14/6/17.
 */

public class Category {

    String id;
    String name;
    byte[] image;

    ArrayList<Category> subCategory = new ArrayList<Category>();
    ArrayList<Items> items= new ArrayList<Items>();

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Category> getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(ArrayList<Category> subCategory) {
        this.subCategory = subCategory;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;
}
