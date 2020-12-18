package com.manza.payment;

import android.app.Activity;
import android.util.Log;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * author : yongfeng.li
 * date : 2020/12/16 13:32
 * description :微信支付
 */
public class WxPay implements IPay {
    private static final String TAG = "WxPay";

    private static WxPay mInstance;
    private final IWXAPI api;

    private WxPay(WeakReference<Activity> weakReference, String appId) {
        api = WXAPIFactory.createWXAPI(weakReference.get(), appId);
    }

    public static WxPay getInstance(WeakReference<Activity> activityWeakReference, String appId) {
        if (mInstance == null) {
            synchronized (WxPay.class) {
                if (mInstance == null) {
                    mInstance = new WxPay(activityWeakReference, appId);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void pay(String orderInfo) {

        JSONObject json;
        try {
            json = new JSONObject(orderInfo);
            if (!json.has("retcode")) {
                PayReq req = new PayReq();
                req.appId = json.getString("appid");
                req.partnerId = json.getString("partnerid");
                req.prepayId = json.getString("prepayid");
                req.nonceStr = json.getString("noncestr");
                req.timeStamp = json.getString("timestamp");
                req.packageValue = json.getString("package");
                req.sign = json.getString("sign");
                req.extData = "app data"; // optional
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                api.sendReq(req);
            } else {
                Log.d(TAG, "返回错误" + json.getString("retmsg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
