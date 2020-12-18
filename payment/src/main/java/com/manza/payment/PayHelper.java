package com.manza.payment;

import android.app.Activity;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * author : yongfeng.li
 * date : 2020/12/16 17:50
 * description :支付帮助类
 */
public class PayHelper {
    public static final String PAY_ALI = "pay_ali";
    public static final String PAY_WX = "pay_wx";
    private static PayHelper payHelper;
    private WeakReference<Activity> activityWeakReference;
    private final ArrayList<PayListener> mPayListeners = new ArrayList<>();

    private PayHelper() {
    }

    private PayHelper(WeakReference<Activity> activityWeakReference) {
        this.activityWeakReference = activityWeakReference;
    }

    public static PayHelper getInstance(WeakReference<Activity> activityWeakReference) {
        if (payHelper == null) {
            synchronized (PayHelper.class) {
                if (payHelper == null) {
                    payHelper = new PayHelper(activityWeakReference);
                }
            }
        }
        return payHelper;
    }


    public void pay(String payType, String info, String appId) {
        if (TextUtils.isEmpty(info)) return;
        switch (payType) {
            case PAY_ALI:
                AliPay.getInstance(activityWeakReference).pay(info);
                break;
            case PAY_WX:
                WxPay.getInstance(activityWeakReference, appId).pay(info);
                break;
            default:
                throw new IllegalArgumentException("不支持的支付类型");
        }
    }

    public void pay(String payType, String info) {
        pay(payType, info, null);
    }

    public void addPayListener(PayListener payListener) {
        if (!mPayListeners.contains(payListener)) {
            mPayListeners.add(payListener);
        }
    }

    public void removePayListener(PayListener payListener) {
        mPayListeners.remove(payListener);
    }

    public void invokePaySuccess() {
        for (PayListener payListener :
                mPayListeners) {
            payListener.onSuccess();
        }
    }

    public void invokePayError() {
        for (PayListener payListener :
                mPayListeners) {
            payListener.onError();
        }
    }

    public void invokePayCancel() {
        for (PayListener payListener :
                mPayListeners) {
            payListener.onCancel();
        }
    }
}
