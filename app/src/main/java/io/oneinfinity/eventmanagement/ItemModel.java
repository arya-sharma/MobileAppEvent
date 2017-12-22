package io.oneinfinity.eventmanagement;

/**
 * Created by ancha on 12/17/2017.
 */

public class ItemModel {

     private String itemId;
     private String itemName;
     private String merchantId;
     private String eventid;
     private float itemPrice;
     private String itemImage;
     private String currency;
     private int itemCount;
     private static ItemModel[] items;

     ItemModel() {

     }

     public static void setItems(ItemModel[] items){
         ItemModel.items = items;
     }

     public static ItemModel[] getItems(){
         return ItemModel.items;
     }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
