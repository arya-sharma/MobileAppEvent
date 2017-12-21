package io.oneinfinity.eventmanagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by ujjwal on 12/20/2017.
 */

public class ChargeActivity extends AppCompatActivity {

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int MY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_charge_layout);

        cameraView = (SurfaceView)findViewById(R.id.camera_view);
        barcodeInfo = (TextView)findViewById(R.id.qr_charge_title);
    }



    @Override
    protected void onStart() {

        super.onStart();
        Button cancel = (Button)findViewById(R.id.charge_cancel);
        cancel.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.w("Cancelling", "cancelled");
                        finish();
                    }
                });

        Log.d("Scanning", "Clicked");
        barcodeDetector =
                new BarcodeDetector.Builder(ChargeActivity.this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(ChargeActivity.this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    cameraSource.start(cameraView.getHolder());
                }
                catch (SecurityException e) {
                    Log.d("CAMERA SOURCE", e.getMessage());
                }
                catch (IOException ie) {
                    Log.d("CAMERA SOURCE", ie.getMessage());
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            int detectionCount = 0;

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                Log.w("Detections", String.valueOf(detectionCount));
                if (barcodes.size() != 0 ) {
                    detectionCount++;
                    if(detectionCount > 1) {
                        return;
                    }
//                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
//                        public void run() {
////                            barcodeInfo.setText(    // Update the TextView
////                                    barcodes.valueAt(0).displayValue
////                            );
//                        }
//                    });
                    Log.d("private key", barcodes.valueAt(0).displayValue);
                    float total = 0;
                    ArrayList<LineItems> items;
                    if(CheckOutCartModel.getCart() != null) {
                        items = CheckOutCartModel.getCart().getLineItems();
                        for(LineItems item: items){
                            total = total + item.getItemPrice()*item.getItemCount();
                        }

                        CheckoutService service = new CheckoutService(barcodes.valueAt(0).displayValue,
                                total, items);
                        String res = service.execute();
                        if(res == "success"){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.w("Running dialog", "yes");
                                    new SweetAlertDialog(ChargeActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Order Placed!")
                                            .setContentText("Click to go to Main menu!")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    finish();
                                                    Intent mainIntent = new Intent(ChargeActivity.this,
                                                            MainActivity.class);
                                                    ChargeActivity.this.startActivity(mainIntent);
                                                }
                                            })
                                            .show();
                                }
                            });

                        }
                    }

                }
            }
        });



    }



}
