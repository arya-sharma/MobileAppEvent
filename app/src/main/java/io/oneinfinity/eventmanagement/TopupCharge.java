package io.oneinfinity.eventmanagement;

/**
 * Created by ancha on 12/21/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

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

public class TopupCharge extends AppCompatActivity {

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int MY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_topup);

        cameraView = (SurfaceView)findViewById(R.id.camera_view_top);
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

        barcodeDetector =
                new BarcodeDetector.Builder(TopupCharge.this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(TopupCharge.this, barcodeDetector)
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
            boolean callMade = false;

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                Log.w("Detections", String.valueOf(detectionCount));
                Log.w("Barcodes", String.valueOf(barcodes.size()));

                if (barcodes.size() != 0 ) {
                    Log.d("private key", barcodes.valueAt(0).displayValue);
                    detectionCount++;
                    Log.w("NewDetections", String.valueOf(detectionCount));
                    if(callMade) {
                        return;
                    }

                        TopupService service = new TopupService(barcodes.valueAt(0).displayValue,
                                TopupModel.amount);
                        callMade = true;
                        final String res = service.execute();

                        if(res == "success"){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new SweetAlertDialog(TopupCharge.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Topup Complete!")
                                            .setContentText("Click to go to Main menu!")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    finish();
                                                    Intent mainIntent = new Intent(TopupCharge.this,
                                                            MainActivity.class);
                                                    TopupCharge.this.startActivity(mainIntent);
                                                }
                                            })
                                            .show();
                                }
                            });

                        }
                        else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new SweetAlertDialog(TopupCharge.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops!")
                                                .setContentText(res)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation();
                                                        finish();
                                                        Intent mainIntent = new Intent(TopupCharge.this,
                                                                TopupActivity.class);
                                                        TopupCharge.this.startActivity(mainIntent);
                                                    }
                                                })
                                                .show();
                                    }
                                });


                        }


                }
            }
        });



    }



}

