package com.bizsoft.pos.dataobject;

import android.widget.GridView;

import com.bizsoft.pos.adapter.ItemOrderedAdapter;
import com.bizsoft.pos.adapter.PurchaseAdapter;
import com.bizsoft.pos.adapter.ShowBillAdapter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by shri on 13/6/17.
 */

public class Store {

    private static Store instance = null;
    public User user = new User();
    public ArrayList<Category> categoryList = new ArrayList<Category>();
    public GridView categoryGridView;
    public ArrayList<Items> itemList;
    public Category currentCategory;
    public ArrayList<Category> simpleCategoryList = new ArrayList<Category>();
    public SupportDetaills supportDetaills;
    public ArrayList<Stock> stock = new ArrayList<Stock>();
    public ArrayList<Stock> stockList = new ArrayList<Stock>();
    public ArrayList<Category> categoryListChart;
    public ArrayList<Items> itemListChart;

    public ArrayList<Vendor> simpleVendorList;
    public ArrayList<Items> simpleItemList;
    public ArrayList<Items> simpleItemListTemp =  new ArrayList<Items>();
    public ItemOrderedAdapter itemAdapterTemp;
    public float subTotal =0;
    public double gst =0;
    public double grandTotal =0;
    public ArrayList<Purchase> purchaseList = new ArrayList<Purchase>();
    public Purchase currentPurchaseOrder;
    public int currentPurchaseOrderPosition;
    public ArrayList<User>                                                                                                                                                                                                                                                                                                                                                                                                                                  customerList = new ArrayList<User>();
    public ArrayList<Items> billingItemList = new ArrayList<Items>();
    public ArrayList<Items> addedItemList = new ArrayList<Items>();
    public String currentCustomerID;
    public String currentstockHomeID;
    public Bill bill;
    public User currentCustomer;
    public ArrayList<Cart> cartList = new ArrayList<Cart>();
    public int currentCartPosition;
    public ArrayList<Bill> billList = new ArrayList<Bill>();
    public int currentBillPosition;
    public Items currentItem;
    public boolean bluetoothStatus;
    public gst gstDO;
    public String screenSize;
    public String LARGE_SCREEN = "large";
    public String MEDIUM_SCREEN = "medium";
    public String SMALL_SCREEN = "small";
    public int textSizeLarge = 20;
    public PurchaseAdapter purchaseListAdapter;
    public ShowBillAdapter billListAdapter;



    private Store() {
        // Exists only to defeat instantiation.
    }

    public static Store getInstance() {
        if(instance == null) {
            instance = new Store();
        }
        return instance;
    }

    public String mode;
    public String baseUrl = "http://192.168.1.12:8080/bizpos/";

}
