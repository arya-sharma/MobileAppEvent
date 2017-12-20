package io.oneinfinity.eventmanagement;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Created by ujjwal on 12/17/2017.
 */

public class MainActivity extends AppCompatActivity {

    Intent loginIntent = null;
    Intent itemIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        if(CheckOutCartModel.getCart() != null) {
            CheckOutCartModel.getCart().resetCart();
        }
        ItemModel.setItems(null);
    }

    public void onGoToItems(View view) {

        this.startActivity(itemIntent);

    }

    public void onTopUp(View view) {
        final EditText txtUrl = new EditText(this);
        txtUrl.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

// Set the default text to a link of the Queen
        txtUrl.setHint("password");
//http://code2care.org/pages/android-alertdialog-programatically-example/
        new AlertDialog.Builder(this)
                .setTitle("Enter Password")
                .setMessage("Please enter your password")
                .setView(txtUrl)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String password = txtUrl.getText().toString();
                        Log.w("Password",JwtModel.password);
                        if(JwtModel.password == password) {
                            //dialogTop.setMessage("");
                            dialog.dismiss();
                        }
                        else{
                            Log.w("Wrong", password);

                            txtUrl.setText("Wrong Password");
                        }
                        Log.w("Entered value", password);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.w("Entered value", "cancelled");
                        dialog.cancel();
                    }
                })
                .show();
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
