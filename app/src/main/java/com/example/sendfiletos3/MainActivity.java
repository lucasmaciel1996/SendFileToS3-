package com.example.sendfiletos3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.internal.Constants;

import java.io.File;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button btnUpload;
    private TransferUtility transferUtility;
    private static final String TAG = MainActivity.class.getSimpleName();
    private utilAws utilAws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permission,0);


        transferUtility = CognitoSetting.getStransferUtility(this);
        utilAws = new utilAws(this, transferUtility);
        Log.i("MAIN",""+ transferUtility.getTransfersWithType(TransferType.UPLOAD));
        initUI();

    }

    private void initUI() {
        btnUpload = (Button) findViewById(R.id.buttonUploadMain);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path = new File(Environment.getExternalStorageDirectory(),"filesLCM/video_1.mp4");//, arquivo.jpg
                Log.d("FILE",""+path.getAbsolutePath());


                utilAws.beginUpload(path.getAbsolutePath());
            }
        });
    }



}
