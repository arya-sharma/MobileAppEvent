package io.oneinfinity.eventmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ancha on 12/21/2017.
 */

public class TopupActivity extends AppCompatActivity {

    private EditText inputAmount;
    private Button confirm;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup_activity);

        inputAmount = findViewById(R.id.input_amount_top);
        confirm = findViewById(R.id.input_confirm);
        cancel = findViewById(R.id.input_cancel);
        setupHandlers();
    }

    private void setupHandlers() {

        confirm.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if(TopupActivity.this.inputAmount.getText().toString().length() !=0){
                            int amount = Integer.parseInt(TopupActivity.this.inputAmount.getText().toString());
                            if(amount < 10) {
                                //TopupActivity.this.inputAmount.setText(0);
                                TopupActivity.this.inputAmount.setHint("Enter amount more than 10");
                            }
                            else {
                                Log.w("Calling", "Calling");
                                Intent intent = new Intent(TopupActivity.this, TopupCharge.class);
                                new TopupModel(amount);
                                TopupActivity.this.startActivity(intent);
                            }
                        }

                    }
                });

        cancel.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.w("Cancelled", "Cencelled");
                        finish();
                    }
                });

    }

}
