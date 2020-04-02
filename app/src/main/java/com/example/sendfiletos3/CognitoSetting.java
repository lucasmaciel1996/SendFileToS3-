package com.example.sendfiletos3;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.util.concurrent.CountDownLatch;

public class CognitoSetting {
    public static final String TAG = CognitoSetting.class.getSimpleName();

    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredPorvider;
    private static TransferUtility stransferUtility;
    private static AWSCredentialsProvider sMobileClient;

    private static AWSCredentialsProvider getCredProvider (Context context) {
        if(sMobileClient == null) {
            final CountDownLatch latch = new CountDownLatch(1);
            AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails result) {
                    Log.e(TAG, "Result: "+result);
                    latch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "onError: ", e);
                    latch.countDown();
                }
            });
            try {
                latch.await();
                //READ CONFIG FILE res/raw/awsconfiguration.json
                sMobileClient = AWSMobileClient.getInstance();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return sMobileClient;
    }

    public static AmazonS3Client getS3Client(Context context) {
        if(sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
            sS3Client.setRegion(Region.getRegion(Regions.fromName("us-east-1")));
        }
        return sS3Client;
    }

    public static TransferUtility getStransferUtility(Context context) {
        if(stransferUtility == null) {
            stransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()), context.getApplicationContext());
        }
        return stransferUtility;
    }
}
