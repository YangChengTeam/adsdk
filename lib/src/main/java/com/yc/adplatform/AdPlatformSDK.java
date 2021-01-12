package com.yc.adplatform;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.tencent.mmkv.MMKV;
import com.yc.adplatform.ad.core.AdCallback;
import com.yc.adplatform.ad.core.AdConfigInfo;
import com.yc.adplatform.ad.core.AdError;
import com.yc.adplatform.ad.core.AdType;
import com.yc.adplatform.ad.core.InitAdCallback;
import com.yc.adplatform.ad.core.SAdSDK;
import com.yc.adplatform.ad.ttad.STtAdSDk;
import com.yc.adplatform.log.AdLog;

public class AdPlatformSDK {
    private static final String TAG = "AdPlatformSDK";

    public static AdPlatformSDK sInstance;

    public static AdPlatformSDK getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdPlatformSDK.class) {
                if (sInstance == null) {
                    sInstance = new AdPlatformSDK(context);
                }
            }
        }
        return sInstance;
    }

    private AdPlatformSDK(Context context) {
        MMKV.initialize(context);
    }

    private AdConfigInfo adConfigInfo;

    private String mAppId;
    private String userId = "0";
    private String adPosition;

    public void setAdPosition(String adPosition) {
        this.adPosition = adPosition;
    }

    public void setAdConfigInfo(AdConfigInfo adConfigInfo) {
        this.adConfigInfo = adConfigInfo;
    }

    public void setmAppId(String mAppId) {
        this.mAppId = mAppId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public interface InitCallback {
        void onAdInitSuccess(); // 广告初始化成功

        void onAdInitFailure(); // 广告初始化失嵊
    }

    public void init(final Context context, String appId, final InitCallback initCallback) {
        this.mAppId = appId;
        SAdSDK.getImpl().initAd(context, adConfigInfo, new InitAdCallback() {
            @Override
            public void onSuccess() {
                SAdSDK.getImpl().setAdConfigInfo(adConfigInfo);
                if (initCallback != null) {
                    initCallback.onAdInitSuccess();
                }
                Log.d(TAG, "adinit: 广告初始化成功");
            }

            @Override
            public void onFailure(AdError adError) {
                if (initCallback != null) {
                    initCallback.onAdInitFailure();
                }
                Log.d(TAG, "adinit: 广告初始化失败 " + adError.getMessage());
            }
        });
    }

    private void sendClickLog(String adPosition, String adCode) {
        if (adConfigInfo == null) return;
        AdLog.sendLog(adConfigInfo.getIp(), 41234, mAppId, userId, adPosition, adCode, "click");
    }

    private void sendShowLog(String adPosition, String adCode) {
        if (adConfigInfo == null) return;
        AdLog.sendLog(adConfigInfo.getIp(), 41234, mAppId, userId, adPosition, adCode, "show");
    }

    private void showAd(Context context, AdType adType, final String adPosition, String adCode, AdCallback callback, FrameLayout containerView) {
        if (adConfigInfo == null) return;
        if (!adConfigInfo.isOpen()) {
            Log.d(TAG, "广告未开启");
            return;
        }

        STtAdSDk.getImpl().showAd(context, adType, new AdCallback() {
            @Override
            public void onDismissed() {
                if (callback != null) {
                    callback.onDismissed();
                }
                AdPlatformSDK.this.adPosition = null;
            }

            @Override
            public void onNoAd(AdError adError) {
                if (callback != null) {
                    callback.onNoAd(adError);
                }
            }

            @Override
            public void onComplete() {
                if (callback != null) {
                    callback.onComplete();
                }

            }

            @Override
            public void onPresent() {
                if (callback != null) {
                    callback.onPresent();
                }
                String tmpAdPosition = adPosition;
                if(AdPlatformSDK.this.adPosition != null){
                    tmpAdPosition = AdPlatformSDK.this.adPosition;
                }
                sendShowLog(tmpAdPosition, adCode);
            }

            @Override
            public void onClick() {
                if (callback != null) {
                    callback.onClick();
                }
                String tmpAdPosition = adPosition;
                if(AdPlatformSDK.this.adPosition != null){
                    tmpAdPosition = AdPlatformSDK.this.adPosition;
                }
                sendClickLog(tmpAdPosition, adCode);
            }

            @Override
            public void onLoaded() {
                if (callback != null) {
                    callback.onLoaded();
                }
            }
        }, containerView);
    }

    private void showAd(Context context, AdType adType, String adPosition, String adCode, AdCallback callback) {
        showAd(context, adType, adPosition, adCode, callback, null);
    }

    public void loadBannerAd(Context context, String adPosition, int width, int height, AdCallback callback, FrameLayout containerView) {
        if (adConfigInfo == null) return;
        STtAdSDk.getImpl().setBannerSize(width, height);
        String adCode = adConfigInfo.getBanner();
        showAd(context, AdType.BANNER, adPosition, adCode, callback, containerView);
    }

    public void loadInsertAd(Context context, String adPosition, int width, int height, AdCallback callback) {
        if (adConfigInfo == null) return;
        STtAdSDk.getImpl().setInsertSize(width, height);
        String adCode = adConfigInfo.getInster();
        showAd(context, AdType.INSERT, adPosition, adCode, callback);
    }

    public void loadExpressAd(Context context, String adPosition,int width, int height, AdCallback callback, FrameLayout containerView) {
        if (adConfigInfo == null) return;
        STtAdSDk.getImpl().setExpressSize(width, height);
        String adCode = adConfigInfo.getExpress();
        showAd(context, AdType.EXPRESS, adPosition, adCode, callback, containerView);
    }

    public void loadFullScreenVideoVerticalAd(Context context, String adPosition, AdCallback callback) {
        if (adConfigInfo == null) return;
        String adCode = adConfigInfo.getFullScreenVideoVertical();
        showAd(context, AdType.FULL_SCREEN_VIDEO_VERTICAL, adPosition, adCode, callback);
    }

    public void loadFullScreenVideoHorizontalAd(Context context,String adPosition, AdCallback callback) {
        if (adConfigInfo == null) return;
        String adCode = adConfigInfo.getFullScreenVideoHorizontal();
        showAd(context, AdType.FULL_SCREEN_VIDEO_HORIZON, adPosition, adCode, callback);
    }

    public void loadRewardVideoVerticalAd(Context context, String adPosition, AdCallback callback) {
        if (adConfigInfo == null) return;
        String adCode = adConfigInfo.getRewardVideoVertical();
        showAd(context, AdType.REWARD_VIDEO_VERTICAL, adPosition, adCode, callback);
    }

    public void loadRewardVideoHorizontalAd(Context context, String adPosition, AdCallback callback) {
        if (adConfigInfo == null) return;
        String adCode = adConfigInfo.getRewardVideoHorizontal();
        showAd(context, AdType.REWARD_VIDEO_HORIZON, adPosition, adCode, callback);
    }

    public void showSplashAd(Context context, String adPosition, int width, int height, AdCallback callback, FrameLayout containerView) {
        if (adConfigInfo == null) return;
        String adCode = adConfigInfo.getSplash();
        STtAdSDk.getImpl().setSplashSize(width, height);
        showAd(context, AdType.SPLASH, adPosition, adCode, callback, containerView);
    }

    public void showSplashVerticalAd(Context context, String adPosition, AdCallback callback, FrameLayout containerView) {
        if (adConfigInfo == null) return;
        String adCode = adConfigInfo.getSplash();
        STtAdSDk.getImpl().setSplashSize(1080, 1920);
        showAd(context, AdType.SPLASH, adPosition, adCode, callback, containerView);
    }

    public void showSplashHorizontalAd(Context context, String adPosition, AdCallback callback, FrameLayout containerView) {
        if (adConfigInfo == null) return;
        STtAdSDk.getImpl().setSplashSize(1920, 1080);
        String adCode = adConfigInfo.getSplash();
        showAd(context, AdType.SPLASH, adPosition, adCode, callback, containerView);
    }

    public boolean showBannerAd() {
       return STtAdSDk.getImpl().showBannerAd();
    }

    public boolean showInsertAd() {
        return STtAdSDk.getImpl().showInteractionAd();
    }

    public boolean showExpressAd() {
        return STtAdSDk.getImpl().showExpressAd();
    }

    public boolean showFullScreenAd() {
        return STtAdSDk.getImpl().showFullScreenAd();
    }

    public boolean showRewardVideoAd() {
        return STtAdSDk.getImpl().showRewardVideoAd();
    }

}
