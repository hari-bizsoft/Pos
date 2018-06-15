package com.bizsoft.pos.dataobject;

import java.util.ArrayList;

/**
 * Created by shri on 17/6/17.
 */

public class SupportDetaills {

    ArrayList<Vendor> vendorList = new ArrayList<Vendor>();

    public ArrayList<Vendor> getVendorList() {
        return vendorList;
    }

    public void setVendorList(ArrayList<Vendor> vendorList) {
        this.vendorList = vendorList;
    }

    public ArrayList<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public ArrayList<StockHome> getStockHomeList() {
        return stockHomeList;
    }

    public void setStockHomeList(ArrayList<StockHome> stockHomeList) {
        this.stockHomeList = stockHomeList;
    }

    ArrayList<Category> categoryList= new ArrayList<Category>();
    ArrayList<StockHome> stockHomeList= new ArrayList<StockHome>();
}
