package io.oneinfinity.eventmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ujjwal on 12/19/2017.
 */

public class CheckoutActivity extends AppCompatActivity {

    Intent loginIntent = null;
    Intent itemIntent = null;
    LinearLayout mainView;
    HashMap<String, View> mapView = new HashMap<>();
    ArrayList<LineItems> items = new ArrayList<>();
    View grandTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainView = (LinearLayout) findViewById(R.id.checkout_form);
        onStartActivity();
    }

    public void onGoToItems(View view) {

        this.startActivity(itemIntent);

    }

    public void onStartActivity() {

        TextView textview;
        TextView priceview;
        int imageWidth = 0;
        int imageHeight = 0;
        int size = DeviceSize.width;
        if(size <= 480) {
            imageHeight = 100;
            imageWidth = 60;
        }
        if(size <= 768 && size > 480) {
            imageHeight = 120;
            imageWidth = 80;
        }
        if(size <= 1080 && size > 768) {
            imageHeight = 180;
            imageWidth = 100;
        }
        if(size > 1080) {
            imageHeight = 200;
            imageWidth = 120;
        }
        float totalAmount = 0;
        if(CheckOutCartModel.getCart() != null) {
            items = CheckOutCartModel.getCart().getLineItems();
            for(LineItems item: items){
                //calculate total
                totalAmount = totalAmount + item.getItemCount()*item.getItemPrice();
                View view = getLayoutInflater().inflate(R.layout.checkout_items, mainView, false);
                ImageView picture = (ImageView)view.findViewById(R.id.picture);
                String url = BuildConfig.IMAGE_URL + item.getItemImage();

                new AsyncTaskLoadImage(picture, imageWidth, imageHeight).execute(url);
                mapView.put(item.getItemId(), view);
                textview = (TextView)view.findViewById(R.id.line);
                //4 X Beer (Rs. 50)
                textview.setText(item.getItemCount()+ " X " + item.getItemName() + " " + item.getCurrency() +" " + item.getItemPrice());

                //Total: Rs. 200
                priceview = (TextView)view.findViewById(R.id.price);
                priceview.setText("Total: " + " " + item.getCurrency() +" " + item.getItemPrice()*item.getItemCount());

                ImageButton add = (ImageButton)view.findViewById(R.id.add);
                add.setTag(item.getItemId());
                ImageButton minus = (ImageButton)view.findViewById(R.id.minus);
                minus.setTag(item.getItemId());

                add.setOnClickListener(
                    new ImageButton.OnClickListener() {
                        public void onClick(View v) {
                            String itemId = (String)v.getTag();
                            float total = 0;
                            for(LineItems item: items) {
                                if(item.getItemId() == itemId) {
                                    item.setItemCount(item.getItemCount() + 1);
                                    CheckOutCartModel.getCart().setLineItems(items);
                                    View view = mapView.get(itemId);
                                    TextView textview = (TextView)view.findViewById(R.id.line);
                                    //4 X Beer (Rs. 50)
                                    textview.setText(item.getItemCount()+ " X " + item.getItemName() + " Rs. " + item.getItemPrice());

                                    //Total: Rs. 200
                                    TextView priceview = (TextView)view.findViewById(R.id.price);
                                    priceview.setText("Total: " + "Rs." + item.getItemPrice()*item.getItemCount());

                                }
                                total = total + item.getItemCount()*item.getItemPrice();
                            }
                            updateTotal(total);
                        }
                    }
                );

                minus.setOnClickListener(
                    new ImageButton.OnClickListener() {
                        public void onClick(View v) {
                            String itemId = (String)v.getTag();
                            float total = 0;
                            for (Iterator<LineItems> iterator = items.iterator(); iterator.hasNext(); ) {
                                LineItems item = iterator.next();
                                if(item.getItemId() == itemId) {
                                    item.setItemCount(item.getItemCount() - 1);
                                    CheckOutCartModel.getCart().setLineItems(items);
                                    View view = mapView.get(itemId);
                                    TextView textview = (TextView)view.findViewById(R.id.line);
                                    //4 X Beer (Rs. 50)
                                    textview.setText(item.getItemCount()+ " X " + item.getItemName() + " Rs. " + item.getItemPrice());

                                    //Total: Rs. 200
                                    TextView priceview = (TextView)view.findViewById(R.id.price);
                                    priceview.setText("Total: " + "Rs." + item.getItemPrice()*item.getItemCount());
                                }
                                if(item.getItemCount() == 0) {
                                    iterator.remove();
                                    mainView.removeView(mapView.remove(itemId));
                                    CheckOutCartModel.getCart().setLineItems(items);
                                }
                                total = total + item.getItemCount()*item.getItemPrice();
                            }
                            updateTotal(total);
                        }
                    }
                );
                mainView.addView(view);
            }

        }

        createGrandTotal(totalAmount);
    }

    public void createGrandTotal(float total) {
        grandTotal = getLayoutInflater().inflate(R.layout.grand_total, mainView, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 180);
        layoutParams.setMargins(0, 40, 0, 10);
        grandTotal.setLayoutParams(layoutParams);
        TextView text = (TextView)grandTotal.findViewById(R.id.grand_checkout);
        text.setText("Grand Total: Rs." + total );
        mainView.addView(grandTotal);
        Button charge = new Button(this);
        charge.setText("Charge");
        charge.setBackgroundColor(Color.parseColor("#ff6624"));
        charge.setTextColor(Color.WHITE);

        //Charge intent
        charge.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent chargeIntent = new Intent(CheckoutActivity.this, ChargeActivity.class);
                        CheckoutActivity.this.startActivity(chargeIntent);
                    }

                });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 20;
        mainView.addView(charge, params);
        Button discard = (Button)grandTotal.findViewById(R.id.discard);
        discard.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        Intent mainIntent = new Intent(CheckoutActivity.this, MainActivity.class);
                        CheckoutActivity.this.startActivity(mainIntent);
                    }

                });

    }

    public void updateTotal(float total) {
        TextView grand = (TextView)grandTotal.findViewById(R.id.grand_checkout);
        grand.setText("Grand Total: Rs." + total );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        loginIntent = new Intent(this, LoginActivity.class);
        itemIntent = new Intent(this, ItemActivity.class);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item1:
                JwtModel token = new JwtModel("", "");
                this.startActivity(loginIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
