package com.yc.adplatformsdkexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.whychl.TrickyCastle.R;
import com.yc.adplatform.AdPlatformSDK;
import com.yc.adplatform.ad.core.AdCallback;
import com.yc.adplatform.ad.core.AdError;

public class SplashActivity extends AppCompatActivity {

    private PermissionHelper permissionHelper;
    private FrameLayout mFrameLayout;
    private boolean isAdClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        permissionHelper = new PermissionHelper();
        permissionHelper.checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {

            }

            @Override
            public void onRequestPermissionError() {

            }
        });
        mFrameLayout = findViewById(R.id.fl_ad_container);

        showSplash();
    }


    private void showSplash() {
        AdPlatformSDK.getInstance(this).showSplashHorizontalAd(this, "ad_splash",new AdCallback() {
            @Override
            public void onDismissed() {
                startMainActivity(0);
            }

            @Override
            public void onNoAd(AdError adError) {
                startMainActivity(0);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onPresent() {
                Log.d("00671 securityhttp ", "showSplash onPresent: ");
            }

            @Override
            public void onClick() {
                isAdClick = true;
            }

            @Override
            public void onLoaded() {

            }
        }, mFrameLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdClick) {
            isAdClick = false;
            startMainActivity(500);
        }
    }

    private void startMainActivity(long dealy) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.onRequestPermissionsResult(this, requestCode);
    }
}