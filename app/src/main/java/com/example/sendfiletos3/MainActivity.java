package com.example.sendfiletos3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.example.sendfiletos3.menssage.ErrorEvent;
import com.example.sendfiletos3.menssage.ProgressChangedEvent;
import com.example.sendfiletos3.menssage.StateChangedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnUpload, btnFind;
    private TransferUtility transferUtility;
    private utilAws utilAws;
    private TextView txt_status;
    private static int RESULT_LOAD_FILE = 1;
    private String filePath = null;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permission,0);


        transferUtility = CognitoSetting.getStransferUtility(this);
        utilAws = new utilAws(this, transferUtility);
        initUI();

    }
    private void initUI() {
        btnUpload = (Button) findViewById(R.id.buttonUploadMain);
        btnFind = (Button) findViewById(R.id.buttonFindMain);
        txt_status = (TextView) findViewById(R.id.txtv_status);
        img = (ImageView) findViewById(R.id.imv_img);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(i, RESULT_LOAD_FILE);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //File path = new File(Environment.getExternalStorageDirectory(),"filesLCM/video_1.mp4");//, arquivo.jpg
                Log.d("FILE",""+filePath);


                utilAws.beginUpload(filePath);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_FILE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();

            Log.e("FILE", filePath);
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MICRO_KIND);
            img.setImageBitmap(thumbnail);
            btnUpload.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStateChangedEvent(StateChangedEvent event) {
        if(event.state == "COMPLETED") {
            filePath = null;
            btnUpload.setVisibility(View.GONE);
            img.setImageBitmap(null);
        }
        txt_status.setText(event.state);
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErroEvent(ErrorEvent event) {
        txt_status.setText(event.erro);
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStateChanged(ProgressChangedEvent event) {
        DecimalFormat df =  new DecimalFormat();
        df.setMaximumFractionDigits(2);

        double progres = ( (double) event.bytesCurrent / (double) event.bytesTotal);
        float percent = (float) progres *100;
        percent =  Float.parseFloat(df.format(percent).replace(",","."));

        Log.i("PROGRESS","ID"+ event.id+" BYTECURRENT "+event.bytesCurrent/1024+ " BYTETOTAL "+event.bytesTotal/1024+" %"+progres);
        txt_status.setText("Enviado "+ percent +"%");
    };

}
