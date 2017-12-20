package io.oneinfinity.eventmanagement;

import java.util.ArrayList;

/**
 * Created by ujjwal on 12/19/2017.
 */

public class CheckOutCartModel {

    private ArrayList<LineItems> lineItems;
    private static CheckOutCartModel cart;

    private CheckOutCartModel(ArrayList<LineItems> items){
        this.lineItems = items;
    }

    public static CheckOutCartModel getCart(ArrayList<LineItems> items) {
        if(cart == null) {
            cart = new CheckOutCartModel(items);
            return cart;
        }
        else {
            return cart;
        }
    }

    public static CheckOutCartModel getCart(){

        return cart;

    }

    public ArrayList<LineItems> getLineItems() {
        return lineItems;
    }

    public void setLineItems(ArrayList<LineItems> lineItems) {
        this.lineItems = lineItems;
    }

    public void resetCart() {
        cart = null;
    }

}
