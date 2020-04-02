package com.example.sendfiletos3;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

class utilAws {
    private static final String TAG = utilAws.class.getSimpleName();
    private Context context;
    private TransferUtility transferUtility;
    private TextView txtv_status;

    utilAws(Context context, TransferUtility transferUtility) {
        this.context = context;
        this.transferUtility = transferUtility;
    }

    public void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(context, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload("qclass-files-homo", file.getName(), file);
        observer.setTransferListener(new TransferListener() {
            // Simply updates the UI list when notified.
            @Override
            public void onError(int id, Exception e) {
                Log.e(TAG, "Error during upload: " + id, e);


            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.i(TAG, "PROGRESS .."+bytesCurrent);
            }

            @Override
            public void onStateChanged(int id, TransferState newState) {
                Log.i(TAG, "NEW STATE .."+newState);
            }
        });
    }
}
